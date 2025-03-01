package com.sagiziv.connectx.mqtt;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.sagiziv.connectx.mqtt.serializers.JsonSerializer;
import com.sagiziv.connectx.mqtt.serializers.Serializer;
import com.sagiziv.connectx.mqtt.topics.BroadcastTopicBuilder;
import com.sagiziv.connectx.mqtt.topics.Constants;
import com.sagiziv.connectx.mqtt.topics.DirectTopicBuilder;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public final class MqttHandler {

    private static MqttHandler instance;
    private final TopicsTree<IncomingMessage> topicsTree;
    private final String serverUrl;
    private MqttAsyncClient client;
    private String userId, clientId;
    private Runnable onConnected;
    private final Map<Long, Runnable> awaitingMessages;
    private final Handler responsesHandler;

    private MqttHandler(final @NonNull String serverUrl, final int port) {
        Uri uri = new Uri.Builder()
                .scheme("tcp")
                .encodedAuthority(serverUrl + ":" + port)
                .build();
        this.serverUrl = uri.toString();
        topicsTree = new TopicsTree<>();
        awaitingMessages = Collections.synchronizedMap(new HashMap<>());
        responsesHandler = new Handler(Looper.getMainLooper());
    }

    public static void initialize(final @NonNull String serverUrl, final int port) {
        instance = new MqttHandler(serverUrl, port);
    }

    public static MqttHandler getInstance() {
        return instance;
    }

    public void connect(@NonNull String userId, @NonNull String deviceId, Runnable onConnected) throws MqttException {
        client = new MqttAsyncClient(serverUrl, getFullTopic(userId, "_", deviceId), new MemoryPersistence());
        client.setCallback(mqttCallback);
        this.userId = userId;
        this.clientId = deviceId;
        this.onConnected = onConnected;
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(runnable1 -> {
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setAutomaticReconnect(true);
                    options.setUserName(deviceId);
                    options.setPassword(runnable1.getResult().getToken().toCharArray());
                    try {
                        client.connect(options);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void disconnect() throws MqttException {
        client.disconnect();
    }

    public void subscribeTo(final String topicPattern, final Consumer<IncomingMessage> callback) throws MqttException {
        final String fullTopic = getFullTopic(userId, Constants.Separator, topicPattern);
        client.subscribe(fullTopic, 0);
        topicsTree.insertTopicValue(topicPattern, callback);
    }

    public void unsubscribeFrom(final String topicPattern) throws MqttException {
        final String fullTopic = getFullTopic(userId, Constants.Separator, topicPattern);
        client.unsubscribe(fullTopic);
        topicsTree.deleteTopic(topicPattern);
    }

    public void publishTo(final String topic, @NonNull final String payload) throws MqttException {
        final String fullTopic = getFullTopic(userId, Constants.Separator, topic);
        final MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        client.publish(fullTopic, message);
    }

    public void publishTo(final String topic, @NonNull final String payload, boolean retained) throws MqttException {
        final String fullTopic = getFullTopic(userId, Constants.Separator, topic);
        final MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        message.setRetained(retained);
        client.publish(fullTopic, message);
    }

    private final AtomicLong messageIdCounter = new AtomicLong(0);

    public void sendMessageTo(String recipientId, Object payload, String subTopic) throws MqttException {
        String messageId = Long.toString(messageIdCounter.incrementAndGet());
        String topic = new DirectTopicBuilder()
                .addTopicSegment(subTopic)
                .toRecipient(recipientId)
                .withMessageId(messageId)
                .withPayloadFormat("json")
                .withoutStatus()
                .build();
        publishTo(topic, new Gson().toJson(payload));
    }

    public void sendMessageTo(String recipientId, Object payload, String subTopic, MessageResponseCallback responseCallback) throws MqttException {
        long messageIdLong = messageIdCounter.incrementAndGet();
        String messageId = Long.toString(messageIdLong);
        String topic = new DirectTopicBuilder()
                .addTopicSegment(subTopic)
                .toRecipient(recipientId)
                .withMessageId(messageId)
                .withPayloadFormat("json")
                .withoutStatus()
                .build();
        String responseTopicPattern = new DirectTopicBuilder()
                .toRecipient(recipientId)
                .withMessageId(messageId)
                .withPayloadFormat("json")
                .withAnyStatus()
                .addTopicSegment(subTopic)
                .build();
        Log.d("pttt", "Listening to topic: " + responseTopicPattern);
        responseCallback.reset();
        subscribeTo(responseTopicPattern,
                incomingMessage -> {
                    Runnable timeoutCallback = awaitingMessages.get(messageIdLong);
                    responsesHandler.removeCallbacks(timeoutCallback);
                    awaitingMessages.remove(messageIdLong);
                    try {
                        handleResponse(incomingMessage, responseCallback, responseTopicPattern);
                    } catch (MqttException e) {
                        throw new RuntimeException(e);
                    }
                });
        publishTo(topic, new Gson().toJson(payload));

        Runnable timeoutCallback = () -> {
            responseCallback.onTimeout();
            try {
                unsubscribeFrom(responseTopicPattern);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        };
        responsesHandler.postDelayed(timeoutCallback, 5000);
        awaitingMessages.put(messageIdLong, timeoutCallback);
    }

    @NonNull
    private String getFullTopic(String userId, String separator, String topic) {
        return userId + separator + topic;
    }

    private byte extractResponseStatus(String topic) {
        // The topic's format is: <recipient_id>/<message_id>/<payload_format>/<status_code>/<sub_topic>
        // We need the 4th segment
        int startIndex = 0;
        int segmentIndex = 3;
        while (0 < segmentIndex && startIndex != -1) {
            startIndex = topic.indexOf(Constants.Separator, startIndex + 1);
            segmentIndex--;
        }
        if (startIndex == -1) {
            return -1;
        }
        int endIndex = topic.indexOf(Constants.Separator, startIndex + 1);
        if (endIndex == -1) {
            endIndex = topic.length();
        }
        return Byte.parseByte(topic.substring(startIndex + 1, endIndex), 16);
    }

    private void handleResponse(IncomingMessage message, MessageResponseCallback responseCallback, String responsePattern) throws MqttException {
        byte status = extractResponseStatus(message.getTopic());
        try {
            responseCallback.onResponse(message, status);
        } finally {
            if (status != ResponseMessage.ResponseStatus.IN_PROGRESS) {
                unsubscribeFrom(responsePattern);
            }
        }
    }

    private final MqttCallback mqttCallback = new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            if (onConnected != null)
                onConnected.run();
            onConnected = null;
            // Send the connected message
            final String topic = new BroadcastTopicBuilder()
                    .fromSender(clientId)
                    .withPayloadFormat("json")
                    .addTopicSegment("Connected")
                    .build();
            final String payload = new Gson()
                    .toJson(new DeviceSettings("json"));
            try {
                publishTo(topic, payload, true);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void connectionLost(Throwable cause) {

        }

        @Override
        public void messageArrived(final String topic, final MqttMessage message) {
            final String topicWithoutUserId = topic.substring(userId.length() + 1); // Skip the "userId/" segment
            final String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            final String payloadFormat = topicWithoutUserId.split(Constants.Separator)[2];
            final IncomingMessage incoming = new IncomingMessage(topicWithoutUserId, payload, new JsonSerializer());
            topicsTree.getValuesFor(topicWithoutUserId)
                    .parallelStream()
                    .forEach(x -> x.accept(incoming));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    public String getServerUrl() {
        Uri uri = Uri.parse(serverUrl);
        return uri.getAuthority().replaceFirst(":\\d+", "");
    }

    public static final class IncomingMessage {
        private final String topic;
        private final String payload;
        private final Serializer payloadSerializer;

        public IncomingMessage(String topic, String payload, Serializer payloadSerializer) {
            this.topic = topic;
            this.payload = payload;
            this.payloadSerializer = payloadSerializer;
        }

        public String getTopic() {
            return topic;
        }

        public String getPayload() {
            return payload;
        }

        public <T> T getPayload(Type type) {
            return payloadSerializer.Deserialize(getPayload(), type);
        }
    }

    private static final class DeviceSettings {
        private final static int UnlimitedConcurrentRequests = -1;
        private final String inputSerializer;
        private final int maxConcurrentRequests;

        public DeviceSettings(String inputSerializer) {
            this.inputSerializer = inputSerializer;
            maxConcurrentRequests = UnlimitedConcurrentRequests;
        }

        public String getInputSerializer() {
            return inputSerializer;
        }

        public int getMaxConcurrentRequests() {
            return maxConcurrentRequests;
        }
    }

    public static abstract class MessageResponseCallback {
        private static final byte WAITING_FOR_RESPONSE = 10;
        private static final byte RESPONSE_RECEIVED = 20;
        private static final byte WAITING_TIMEOUT = 30;
        private final AtomicInteger responseStatus = new AtomicInteger(WAITING_FOR_RESPONSE);

        public void onResponse(IncomingMessage message, byte status) {
            // If the status is not waiting, it means we got to a timeout and don't need to process the response.
            if (responseStatus.getAndSet(RESPONSE_RECEIVED) != WAITING_FOR_RESPONSE) return;

            if (status == ResponseMessage.ResponseStatus.ERROR)
                onErrorImpl(message.getPayload());
            else
                processResponseImpl(message, status);
        }

        public void onTimeout() {
            // If the status is not waiting, it means we got a response and can ignore the timeout.
            if (responseStatus.getAndSet(WAITING_TIMEOUT) != WAITING_FOR_RESPONSE) return;
            onTimeoutImpl("Target device not responding");
        }

        protected void reset() {
            responseStatus.set(WAITING_FOR_RESPONSE);
        }

        protected abstract void processResponseImpl(IncomingMessage message, byte status);

        protected abstract void onErrorImpl(String error);

        protected abstract void onTimeoutImpl(String error);
    }

}
