package com.sagiziv.connectx.dto;

public class UserCreated {
    private User user;
    private DeviceInfo device;

    public UserCreated() {
    }

    public User getUser() {
        return user;
    }

    public UserCreated setUser(final User user) {
        this.user = user;
        return this;
    }

    public DeviceInfo getDevice() {
        return device;
    }

    public UserCreated setDevice(final DeviceInfo device) {
        this.device = device;
        return this;
    }
}
