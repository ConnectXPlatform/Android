package com.sagiziv.connectx.fragments;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.customviews.Label;
import com.sagiziv.connectx.dto.ControlPanel;


public class ControlPanelViewer {
    private TextView nameLabel, descriptionTextView;
    private Label createdByLabel;

    public ControlPanelViewer(View context) {
        findViews(context);
    }

    public void showControlPanel(@NonNull ControlPanel controlPanel) {
        nameLabel.setText(controlPanel.getName());
        descriptionTextView.setText(controlPanel.getDescription());
        createdByLabel.setText(controlPanel.getCreator());
    }

    private void findViews(@NonNull View root) {
        nameLabel = root.findViewById(R.id.control_panel_carousel_LBL_name);
        createdByLabel = root.findViewById(R.id.control_panel_carousel_LBL_created_by);
        descriptionTextView = root.findViewById(R.id.control_panel_carousel_LBL_description);
    }
}
