package com.sagiziv.connectx.mqtt.topics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectTopicBuilder extends TopicBuilder<DirectTopicBuilder>{
    private String messageId = Constants.SingleLevelWildcard;
    private String recipientId = Constants.SingleLevelWildcard;
    private String status = "";

    public DirectTopicBuilder() {
        super(DirectTopicBuilder.class);
    }

    public DirectTopicBuilder withAnyMessageId()
    {
        messageId = Constants.SingleLevelWildcard;
        return this;
    }

    public DirectTopicBuilder withMessageId(String id)
    {
        messageId = id;
        return this;
    }

    public DirectTopicBuilder toRecipient(String recipient)
    {
        recipientId = recipient;
        return this;
    }

    public DirectTopicBuilder withoutStatus()
    {
        status = "";
        return this;
    }

    public DirectTopicBuilder withAnyStatus()
    {
        status = Constants.SingleLevelWildcard;
        return this;
    }

    public DirectTopicBuilder withStatus(String statusString)
    {
        status = statusString;
        return this;
    }

    @Override
    protected String buildImpl(String subTopic)
    {
        List<String> parts = new ArrayList<>(Arrays.asList(recipientId, messageId, payloadFormat));

        if (status != null && !status.isEmpty())
        {
            parts.add(status);
        }
        parts.add(subTopic);

        return String.join(Constants.Separator, parts);
    }
}
