package com.sagiziv.connectx.fragments;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.customviews.Label;
import com.sagiziv.connectx.dto.DeviceInfo;


public class DeviceInfoCarouselViewer {
    private TextView nameLabel, statusLabel, descriptionTextView;
    private Label statusDateLabel, deviceIdLabel;

    public DeviceInfoCarouselViewer(View context) {
        findViews(context);
    }

    public void showDevice(@NonNull DeviceInfo deviceInfo) {
        nameLabel.setText(deviceInfo.getName());
        int statusStringResource = DeviceViewerUtils.getConnectionStatusStringResource(deviceInfo.getStatus());
        statusLabel.setText(statusStringResource);
        descriptionTextView.setText(deviceInfo.getDescription());
        statusDateLabel.setText(DeviceViewerUtils.getDate(deviceInfo.getStatus().getLastChange()));
        statusDateLabel.setPrefixText(DeviceViewerUtils.getConnectionDateStatusStringResource(deviceInfo.getStatus()));
        deviceIdLabel.setText(deviceInfo.getId());
    }

    private void findViews(@NonNull View root) {
        nameLabel = root.findViewById(R.id.device_carousel_LBL_name);
        statusLabel = root.findViewById(R.id.device_carousel_LBL_status);
        statusDateLabel = root.findViewById(R.id.device_carousel_LBL_status_date);
        deviceIdLabel = root.findViewById(R.id.device_carousel_LBL_id);
        descriptionTextView = root.findViewById(R.id.device_carousel_LBL_description);
    }
}
