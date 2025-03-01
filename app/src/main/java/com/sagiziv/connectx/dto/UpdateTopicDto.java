package com.sagiziv.connectx.dto;

public final class UpdateTopicDto {
    private String topic;
    private String dataType;
    private Integer modeFlags;

    public UpdateTopicDto() {
    }

    public String getTopic() {
        return topic;
    }

    public UpdateTopicDto setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getDataType() {
        return dataType;
    }

    public UpdateTopicDto setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

    public Integer getModeFlags() {
        return modeFlags;
    }

    public UpdateTopicDto setModeFlags(Integer modeFlags) {
        this.modeFlags = modeFlags;
        return this;
    }
    public boolean hasUpdate() {
        return topic != null || dataType != null || modeFlags != null;
    }
}
