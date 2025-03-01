package com.sagiziv.connectx.dto;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String[] devices;
    private String[] controlPanels;

    public User() {
    }

    public User(String id, String name, String[] devices, String[] controlPanels) {
        this.id = id;
        this.name = name;
        this.devices = devices;
        this.controlPanels = controlPanels;
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String[] getDevices() {
        return devices;
    }

    public User setDevices(String[] devices) {
        this.devices = devices;
        return this;
    }

    public String[] getControlPanels() {
        return controlPanels;
    }

    public User setControlPanels(String[] controlPanels) {
        this.controlPanels = controlPanels;
        return this;
    }
}
