package com.sagiziv.connectx.bundlewrappers;

import android.os.Bundle;

import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.User;

public class HomeScreenBundleWrapper extends BundleWrapper {

    private static final String USER_KEY = "USER_KEY";
    private static final String DEVICE_KEY = "DEVICE_KEY";

    public HomeScreenBundleWrapper(Bundle bundle) {
        super(bundle);
    }

    public User getUser() {
        return (User) getBundle().getSerializable(USER_KEY);
    }

    public void setUser(User user) {
        getBundle().putSerializable(USER_KEY, user);
    }

    public DeviceInfo getDeviceInfo() {
        return (DeviceInfo) getBundle().getSerializable(DEVICE_KEY);
    }

    public void setDeviceInfo(DeviceInfo deviceInfo) {
        getBundle().putSerializable(DEVICE_KEY, deviceInfo);
    }
}
