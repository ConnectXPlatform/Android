package com.sagiziv.connectx.dto;

import java.io.Serializable;

public class Status implements Serializable {
    public static final int CONNECTED = 1 << 0;
    public static final int DISCONNECTED = 1 << 1;

    private int value;
    private long lastChange;

    public Status() {
    }

    public Status(int value, long lastChange) {
        this.value = value;
        this.lastChange = lastChange;
    }

    public boolean isConnected() {
        return (value & CONNECTED) == CONNECTED;
    }

    public boolean isDisconnected() {
        return (value & DISCONNECTED) == DISCONNECTED;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getLastChange() {
        return lastChange;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }
}
