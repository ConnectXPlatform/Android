package com.sagiziv.connectx;

import android.util.Log;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.mqtt.MqttHandler;
import com.sagiziv.connectx.mqtt.topics.BroadcastTopic;
import com.sagiziv.connectx.mqtt.topics.BroadcastTopicBuilder;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class DevicesHandler {

    private static DevicesHandler instance;
    private Consumer<String> onDeviceConnected, onDeviceDisconnected;
    private final Map<String, Runnable> stateListeners;

    private DevicesHandler() {
        stateListeners = Collections.synchronizedMap(new HashMap<>());
    }

    public static void initialize() {
        instance = new DevicesHandler();
        String connectedTopic = new BroadcastTopicBuilder()
                .fromAnySender()
                .withAnyPayloadFormat()
                .addTopicSegment("Connected")
                .build();
        String disconnectedTopic = new BroadcastTopicBuilder()
                .fromAnySender()
                .withAnyPayloadFormat()
                .addTopicSegment("Disconnected")
                .build();
        try {
            MqttHandler.getInstance()
                    .subscribeTo(connectedTopic, instance::onDeviceConnected);
            MqttHandler.getInstance()
                    .subscribeTo(disconnectedTopic, instance::onDeviceDisconnected);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public static DevicesHandler getInstance() {
        return instance;
    }

    public void setOnDeviceConnected(Consumer<String> onDeviceConnected) {
        this.onDeviceConnected = onDeviceConnected;
    }

    public void setOnDeviceDisconnected(Consumer<String> onDeviceDisconnected) {
        this.onDeviceDisconnected = onDeviceDisconnected;
    }

    public void listenToDeviceStateChanges(String deviceId, Runnable onStateChanged) {
        stateListeners.put(deviceId, onStateChanged);
    }

    public void removeStateListener(String deviceId){
        stateListeners.remove(deviceId);
    }

    private void onDeviceConnected(@NonNull MqttHandler.IncomingMessage incomingMessage) {
        Log.d("pttt", incomingMessage.getTopic());
        if (incomingMessage.getPayload() == null || incomingMessage.getPayload().isEmpty())
            return;

        BroadcastTopic broadcastTopic = BroadcastTopic.fromString(incomingMessage.getTopic());
        Log.d("pttt", "Device connected: " + broadcastTopic);

        if (onDeviceConnected != null)
            onDeviceConnected.accept(broadcastTopic.getSenderId());
        Runnable listener = stateListeners.getOrDefault(broadcastTopic.getSenderId(), null);
        if (listener != null) listener.run();
    }

    private void onDeviceDisconnected(@NonNull MqttHandler.IncomingMessage incomingMessage) {
        BroadcastTopic broadcastTopic = BroadcastTopic.fromString(incomingMessage.getTopic());
        Log.d("pttt", "Device connected: " + broadcastTopic);

        if (onDeviceDisconnected != null)
            onDeviceDisconnected.accept(broadcastTopic.getSenderId());
        Runnable listener = stateListeners.getOrDefault(broadcastTopic.getSenderId(), null);
        if (listener != null) listener.run();
    }

}
