package com.sagiziv.connectx.mqtt.topics;

import java.util.ArrayList;
import java.util.List;

public abstract class TopicBuilder<T extends TopicBuilder<T>> {
    private final List<String> subTopicSegments;
    protected String payloadFormat = Constants.SingleLevelWildcard;
    private boolean hasMultiLevelWildcard;
    private final Class<T> type;

    protected TopicBuilder(Class<T> type) {
        subTopicSegments = new ArrayList<>(2);
        this.type = type;
    }

    public T addTopicSegment(String topicSegment) {
        if (hasMultiLevelWildcard)
            throw new RuntimeException("Multilevel wildcard must be the last segment");
        subTopicSegments.add(topicSegment);
        return type.cast(this);
    }

    public T addSingleLevelWildcard() {
        if (hasMultiLevelWildcard)
            throw new RuntimeException("Multilevel wildcard must be the last segment");
        subTopicSegments.add(Constants.SingleLevelWildcard);
        return type.cast(this);
    }

    public T addMultiLevelWildcard() {
        if (hasMultiLevelWildcard)
            throw new RuntimeException("Multilevel wildcard must be the last segment");
        subTopicSegments.add(Constants.MultiLevelWildcard);
        hasMultiLevelWildcard = true;
        return type.cast(this);
    }

    public T withAnyPayloadFormat() {
        payloadFormat = Constants.SingleLevelWildcard;
        return type.cast(this);
    }

    public T withPayloadFormat(String format) {
        payloadFormat = format;
        return type.cast(this);
    }

    public String build() {
        return buildImpl(String.join(Constants.Separator, subTopicSegments));
    }

    protected abstract String buildImpl(String subTopic);
}
