package com.sagiziv.connectx.fragments;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.TopicInfo;

public class TopicInfoViewer {
    private TextView topicLabel;
    private Chip dataTypeChip, dataProviderChip, commandsProcessorChip;

    public TopicInfoViewer(@NonNull View view) {
        findViews(view);
    }

    public void displayTopic(@NonNull TopicInfo topicInfo) {
        topicLabel.setText(topicInfo.getTopic());
        dataTypeChip.setText(topicInfo.getDataType());
        dataProviderChip.setVisibility(topicInfo.isDataProvider() ? View.VISIBLE : View.GONE);
        commandsProcessorChip.setVisibility(topicInfo.isCommandsProcessor() ? View.VISIBLE : View.GONE);
    }

    protected void findViews(@NonNull View root) {
        topicLabel = root.findViewById(R.id.topic_info_LBL_topic);
        dataTypeChip = root.findViewById(R.id.topic_info_CHIP_data_type);
        dataProviderChip = root.findViewById(R.id.topic_info_CHIP_data_provider);
        commandsProcessorChip = root.findViewById(R.id.topic_info_CHIP_commands_processor);
    }
}
