package com.sagiziv.connectx.dto;

public class NewUser {

    private String userId;
    private String userName;
    private String deviceDescription;
    private String deviceName;

    public NewUser() {
    }

    public String getUserId() {
        return userId;
    }

    public NewUser setUserId(final String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public NewUser setUserName(final String userName) {
        this.userName = userName;
        return this;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public NewUser setDeviceDescription(final String deviceDescription) {
        this.deviceDescription = deviceDescription;
        return this;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public NewUser setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
        return this;
    }
}
