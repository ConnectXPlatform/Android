package com.sagiziv.connectx.bundlewrappers;

import android.os.Bundle;

import com.sagiziv.connectx.dto.ControlPanel;

public class ControlPanelBundleWrapper extends BundleWrapper {
    private static final String CONTROL_PANEL_KEY = "CONTROL_PANEL_KEY";
    private static final String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    public ControlPanelBundleWrapper(Bundle bundle) {
        super(bundle);
    }

    public ControlPanel getControlPanel() {
        return (ControlPanel) getBundle().getSerializable(CONTROL_PANEL_KEY);
    }

    public void setControlPanel(ControlPanel controlPanel) {
        getBundle().putSerializable(CONTROL_PANEL_KEY, controlPanel);
    }

    public boolean isInEditMode() {
        return getBundle().getBoolean(EDIT_MODE_KEY, false);
    }

    public void setIsInEditMode(boolean value) {
        getBundle().putBoolean(EDIT_MODE_KEY, value);
    }
}
