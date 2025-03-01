package com.sagiziv.connectx.dto;

public final class UpdateControlPanelDto {
    private String name;
    private String description;
    private CollectionUpdateDto components;

    public UpdateControlPanelDto() {
    }

    public String getName() {
        return name;
    }

    public UpdateControlPanelDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UpdateControlPanelDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public CollectionUpdateDto getComponents() {
        return components;
    }

    public UpdateControlPanelDto setComponents(CollectionUpdateDto components) {
        this.components = components;
        return this;
    }
}
