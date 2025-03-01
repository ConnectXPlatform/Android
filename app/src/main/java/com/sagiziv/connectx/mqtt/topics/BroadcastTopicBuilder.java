package com.sagiziv.connectx.mqtt.topics;

public class BroadcastTopicBuilder extends TopicBuilder<BroadcastTopicBuilder> {
    private String senderId = Constants.SingleLevelWildcard;

    public BroadcastTopicBuilder() {
        super(BroadcastTopicBuilder.class);
    }

    public BroadcastTopicBuilder fromAnySender() {
        senderId = Constants.SingleLevelWildcard;
        return this;
    }

    public BroadcastTopicBuilder fromSender(String sender) {
        senderId = sender;
        return this;
    }

    @Override
    protected String buildImpl(String subTopic) {
        return String.join(Constants.Separator,
                Constants.BroadcastMessageRecipient, senderId, payloadFormat, subTopic);
    }
}
