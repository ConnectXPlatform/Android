package com.sagiziv.connectx.mqtt.topics;

public class DirectTopic {
    private final String recipientId, messageId, payloadFormat, subTopic;

    public DirectTopic(String recipientId, String messageId, String payloadFormat, String subTopic) {
        this.recipientId = recipientId;
        this.messageId = messageId;
        this.payloadFormat = payloadFormat;
        this.subTopic = subTopic;
    }

    public static DirectTopic fromString(String topic)
    {
        String[] segments = topic.split(Constants.Separator, 4);
        return new DirectTopic(
                segments[0],
                segments[1],
                segments[2],
                segments[3]
        );
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getPayloadFormat() {
        return payloadFormat;
    }

    public String getSubTopic() {
        return subTopic;
    }
}
