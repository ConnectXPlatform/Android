package com.sagiziv.connectx.dto;

public class Component {
    private String id;
    private String name;
    private Range widthRange;
    private Range heightRange;
    private int mode;

    public Component() {
    }

    public String getId() {
        return id;
    }

    public Component setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Component setName(String name) {
        this.name = name;
        return this;
    }

    public Range getWidthRange() {
        return widthRange;
    }

    public Component setWidthRange(Range widthRange) {
        this.widthRange = widthRange;
        return this;
    }

    public Range getHeightRange() {
        return heightRange;
    }

    public Component setHeightRange(Range heightRange) {
        this.heightRange = heightRange;
        return this;
    }

    public int getMode() {
        return mode;
    }

    public Component setMode(int mode) {
        this.mode = mode;
        return this;
    }
}
