package com.sagiziv.connectx.dto;

public class Range {
    private int min;
    private int max;

    public Range() {
    }

    public int getMin() {
        return min;
    }

    public Range setMin(int min) {
        this.min = min;
        return this;
    }

    public int getMax() {
        return max;
    }

    public Range setMax(int max) {
        this.max = max;
        return this;
    }
}
