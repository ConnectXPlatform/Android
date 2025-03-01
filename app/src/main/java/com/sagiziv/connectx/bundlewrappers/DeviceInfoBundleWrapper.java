package com.sagiziv.connectx.bundlewrappers;

import android.os.Bundle;

import com.sagiziv.connectx.dto.DeviceInfo;

public class DeviceInfoBundleWrapper extends BundleWrapper {
    private static final String DEVICE_KEY = "DEVICE_KEY";
    private static final String EDIT_MODE_KEY = "EDIT_MODE_KEY";
    private static final String DELETABLE_KEY = "DELETABLE_KEY";

    public DeviceInfoBundleWrapper(Bundle bundle) {
        super(bundle);
    }

    public DeviceInfo getDevice() {
        return (DeviceInfo) getBundle().getSerializable(DEVICE_KEY);
    }

    public void setDevice(DeviceInfo deviceInfo) {
        getBundle().putSerializable(DEVICE_KEY, deviceInfo);
    }

    public boolean isInEditMode() {
        return getBundle().getBoolean(EDIT_MODE_KEY, false);
    }

    public void setIsInEditMode(boolean value) {
        getBundle().putBoolean(EDIT_MODE_KEY, value);
    }

    public boolean isDeletable() {
        return getBundle().getBoolean(DELETABLE_KEY, true);
    }

    public void setDeletable(boolean deletable) {
        getBundle().putBoolean(DELETABLE_KEY, deletable);
    }
}
