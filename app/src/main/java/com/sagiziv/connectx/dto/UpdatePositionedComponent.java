package com.sagiziv.connectx.dto;

public final class UpdatePositionedComponent {
    private String componentId;
    private String topicId;
    private String deviceId;
    private String label;
    private Size size;
    private Position position;

    public UpdatePositionedComponent() {
    }

    public String getComponentId() {
        return componentId;
    }

    public UpdatePositionedComponent setComponentId(String componentId) {
        this.componentId = componentId;
        return this;
    }

    public String getTopicId() {
        return topicId;
    }

    public UpdatePositionedComponent setTopicId(String topicId) {
        this.topicId = topicId;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public UpdatePositionedComponent setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public UpdatePositionedComponent setLabel(String label) {
        this.label = label;
        return this;
    }

    public Size getSize() {
        return size;
    }

    public UpdatePositionedComponent setSize(Size size) {
        this.size = size;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public UpdatePositionedComponent setPosition(Position position) {
        this.position = position;
        return this;
    }
}
