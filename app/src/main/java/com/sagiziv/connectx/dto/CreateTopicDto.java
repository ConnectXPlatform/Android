package com.sagiziv.connectx.dto;

public class CreateTopicDto {
    private String topic;
    private int modeFlags;
    private String dataType;

    public CreateTopicDto() {
    }

    public CreateTopicDto setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public CreateTopicDto setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    public CreateTopicDto setModeFlags(int modeFlags) {
        this.modeFlags = modeFlags;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public int getModeFlags() {
        return modeFlags;
    }

    public String getDataType() {
        return dataType;
    }
}
