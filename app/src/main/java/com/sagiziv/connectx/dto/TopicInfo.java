package com.sagiziv.connectx.dto;

public class TopicInfo {
    private String id;
    private String topic;
    private int modeFlags;
    private String dataType;

    public TopicInfo() {
    }

    public TopicInfo(String id, String topic, int modeFlags, String dataType) {
        this.id = id;
        this.topic = topic;
        this.modeFlags = modeFlags;
        this.dataType = dataType;
    }

    public String getId() {
        return id;
    }

    public TopicInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getModeFlags() {
        return modeFlags;
    }

    public void setModeFlags(int modeFlags) {
        this.modeFlags = modeFlags;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isDataProvider(){
        return (modeFlags & TopicModes.DataProvider) == TopicModes.DataProvider;
    }

    public boolean isCommandsProcessor(){
        return (modeFlags & TopicModes.CommandsProcessor) == TopicModes.CommandsProcessor;
    }

    public boolean isBroadcastSender(){
        return (modeFlags & TopicModes.BroadcastSender) == TopicModes.BroadcastSender;
    }

    public static int getTopicModeFlags(boolean isDataProvider, boolean isCommandsProcessor) {
        int type = 0;
        if (isDataProvider)
            type |= TopicInfo.TopicModes.DataProvider;
        if (isCommandsProcessor)
            type |= TopicInfo.TopicModes.CommandsProcessor;
        return type;
    }

    public static final class TopicModes {
        @SuppressWarnings("PointlessBitwiseExpression")
        public static final byte DataProvider = 1 << 0; // 0b001
        public static final byte CommandsProcessor = 1 << 1; // 0b010
        public static final byte BroadcastSender = 1 << 2; // 0b100
        public static final byte InteractiveHandler = DataProvider | CommandsProcessor; // 0b011
    }
}
