package com.sagiziv.connectx.mqtt.topics;

public class BroadcastTopic {
    private final String senderId, payloadFormat, subTopic;

    public BroadcastTopic(String senderId, String payloadFormat, String subTopic) {
        this.senderId = senderId;
        this.payloadFormat = payloadFormat;
        this.subTopic = subTopic;
    }

    public static BroadcastTopic fromString(String topic)
    {
        String[] segments = topic.split(Constants.Separator, 4);
        return new BroadcastTopic(
                segments[1],
                segments[2],
                segments[3]
        );
    }

    public String getSenderId() {
        return senderId;
    }

    public String getPayloadFormat() {
        return payloadFormat;
    }

    public String getSubTopic() {
        return subTopic;
    }

    @Override
    public String toString() {
        return "BroadcastTopic{" +
                "senderId='" + senderId + '\'' +
                ", payloadFormat='" + payloadFormat + '\'' +
                ", subTopic='" + subTopic + '\'' +
                '}';
    }
}
