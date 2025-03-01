package com.sagiziv.connectx.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class DeviceInfo implements Serializable {
    private String id;
    private String name;
    private String description;
    private Status status;
    private ArrayList<String> topics;

    public DeviceInfo() {
    }

    public DeviceInfo(String id, String name, String description, Status status, ArrayList<String> topics) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.topics = topics;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public DeviceInfo setTopics(ArrayList<String> topics) {
        this.topics = topics;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
