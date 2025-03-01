package com.sagiziv.connectx.mqtt;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.sagiziv.connectx.mqtt.topics.Constants;

public class ResponseMessage {
    private final String messageId;
    private final String topic;
    private final String payload;
    private final byte responseStatus;

    public ResponseMessage(String messageId, String topic, String payload, byte responseStatus) {
        this.messageId = messageId;
        this.topic = topic;
        this.payload = payload;
        this.responseStatus = responseStatus;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    public byte getResponseStatus() {
        return responseStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return "ResponseMessage{" +
                "messageId='" + messageId + '\'' +
                ", topic='" + topic + '\'' +
                ", payload=" + payload +
                '}';
    }

    public static ResponseMessage fromIncomingMessage(MqttHandler.IncomingMessage message) {
        // The topic's format is: <recipient_id>/<message_id>/<payload_format>/<status_code>/<sub_topic>
        String[] topicSegments = message.getTopic().split(Constants.Separator, 5);

        return new ResponseMessage
                (
                        topicSegments[1],
                        topicSegments[4],
                        message.getPayload(),
                        Byte.parseByte(topicSegments[3])
                );
    }

    public static final class ResponseStatus {
        public static final byte ERROR = 1;
        public static final byte IN_PROGRESS = 2;
        public static final byte DONE = 3;
        public static final byte EMPTY = 4;
    }
}
