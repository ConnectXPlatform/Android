package com.sagiziv.connectx.dto;

import java.io.Serializable;

public class ControlPanel implements Serializable {
    private String id;
    private String name;
    private String creator;
    private String description;
    private String[] components;

    public ControlPanel() {
    }

    public ControlPanel(String id, String name, String creator, String description, String[] components) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.components = components;
    }

    public String getId() {
        return id;
    }

    public ControlPanel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ControlPanel setName(String name) {
        this.name = name;
        return this;
    }

    public String getCreator() {
        return creator;
    }

    public ControlPanel setCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ControlPanel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String[] getComponents() {
        return components;
    }

    public ControlPanel setComponents(String[] components) {
        this.components = components;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControlPanel that = (ControlPanel) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
