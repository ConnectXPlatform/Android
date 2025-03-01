package com.sagiziv.connectx.dto;

public class UpdateDeviceDto {
    private String name;
    private String description;
    private Status status;

    public UpdateDeviceDto() {
    }

    public String getName() {
        return name;
    }

    public UpdateDeviceDto setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UpdateDeviceDto setDescription(final String description) {
        this.description = description;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public UpdateDeviceDto setStatus(final Status status) {
        this.status = status;
        return this;
    }
    public boolean hasUpdate() {
        return name != null || description != null || status != null;
    }
}
