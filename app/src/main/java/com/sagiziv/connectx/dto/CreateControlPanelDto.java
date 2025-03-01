package com.sagiziv.connectx.dto;

import java.util.ArrayList;
import java.util.List;

public class CreateControlPanelDto {
    private String name;
    private String description;
    private String creator;
    private List<String> topics;

    public CreateControlPanelDto() {
        topics = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public CreateControlPanelDto setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateControlPanelDto setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getCreator() {
        return creator;
    }

    public CreateControlPanelDto setCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public List<String> getTopics() {
        return topics;
    }

    public CreateControlPanelDto setTopics(final List<String> topics) {
        this.topics = topics;
        return this;
    }

    public void addTopic(final String topic) {
        topics.add(topic);
    }
}
