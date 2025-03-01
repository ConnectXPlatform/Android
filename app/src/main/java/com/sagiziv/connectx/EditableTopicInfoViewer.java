package com.sagiziv.connectx;

import android.view.View;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.dto.TopicInfo;
import com.sagiziv.connectx.fragments.TopicInfoViewer;

import java.util.function.Consumer;

public class EditableTopicInfoViewer extends TopicInfoViewer {

    private final View rootView;
    private View backgroundView, deleteButton;
    private Consumer<TopicInfo> onClick, onDelete;
    private TopicInfo editedTopic;

    public EditableTopicInfoViewer(@NonNull View root) {
        super(root);
        this.rootView = root;
        backgroundView.setOnClickListener(this::backgroundClicked);
        deleteButton.setOnClickListener(this::deleteClicked);
    }

    public View getRootView() {
        return rootView;
    }

    @Override
    public void displayTopic(@NonNull TopicInfo topicInfo) {
        super.displayTopic(topicInfo);
        editedTopic = topicInfo;
    }

    public void setOnClick(Consumer<TopicInfo> onClick) {
        this.onClick = onClick;
    }

    public void setOnDelete(Consumer<TopicInfo> onDelete) {
        this.onDelete = onDelete;
    }

    @Override
    protected void findViews(@NonNull View root) {
        super.findViews(root);

        backgroundView = root.findViewById(R.id.editable_topic_LAY_main);
        deleteButton = root.findViewById(R.id.editable_topic_info_IMG_delete);
    }

    private void backgroundClicked(View view) {
        if (onClick != null) onClick.accept(editedTopic);
    }

    private void deleteClicked(View view) {
        if (onDelete != null) onDelete.accept(editedTopic);
    }
}
