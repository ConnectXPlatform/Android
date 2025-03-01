package com.sagiziv.connectx.bundlewrappers;

import android.os.Bundle;

public abstract class BundleWrapper {
    private final Bundle bundle;

    public BundleWrapper(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
