package com.sagiziv.connectx.dto;

import java.util.ArrayList;
import java.util.List;

public class CreateDeviceDto {
    private String name;
    private String description;
    private List<String> topics;

    public CreateDeviceDto() {
        topics = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public CreateDeviceDto setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateDeviceDto setDescription(final String description) {
        this.description = description;
        return this;
    }

    public List<String> getTopics() {
        return topics;
    }

    public CreateDeviceDto setTopics(final List<String> topics) {
        this.topics = topics;
        return this;
    }

    public void addTopic(final String topic) {
        topics.add(topic);
    }
}
