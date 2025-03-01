package com.sagiziv.connectx.dto;

public final class CreatePositionedComponent {

    private String componentId;
    private String topicId;
    private String deviceId;
    private String label;
    private Size size;
    private Position position;

    public CreatePositionedComponent() {
    }

    public String getComponentId() {
        return componentId;
    }

    public CreatePositionedComponent setComponentId(String componentId) {
        this.componentId = componentId;
        return this;
    }

    public String getTopicId() {
        return topicId;
    }

    public CreatePositionedComponent setTopicId(String topicId) {
        this.topicId = topicId;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public CreatePositionedComponent setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public CreatePositionedComponent setLabel(String label) {
        this.label = label;
        return this;
    }

    public Size getSize() {
        return size;
    }

    public CreatePositionedComponent setSize(Size size) {
        this.size = size;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    public CreatePositionedComponent setPosition(Position position) {
        this.position = position;
        return this;
    }
}
