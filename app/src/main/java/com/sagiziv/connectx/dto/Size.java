package com.sagiziv.connectx.dto;

import androidx.annotation.NonNull;

public class Size {
    private final int height, width;

    public Size(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @NonNull
    @Override
    public String toString() {
        return "Size{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }
}
