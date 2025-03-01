package com.sagiziv.connectx.fragments.deviceinfoviewer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.sagiziv.connectx.utils.AndroidUtils;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.customviews.Label;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.Status;
import com.sagiziv.connectx.dto.TopicInfo;
import com.sagiziv.connectx.fragments.DeviceViewerUtils;
import com.sagiziv.connectx.fragments.TopicInfoViewer;
import com.sagiziv.connectx.repositories.TopicsRepository;

import java.util.Objects;

public class DeviceInfoViewerFragment extends AbstractDeviceInfoViewerFragment {

    private Label idLabel, statusDateLabel;
    private TextView statusTextView, descriptionTextView;
    private LinearLayoutCompat topicsList;
    private final TopicsRepository topicsRepository;

    public DeviceInfoViewerFragment(TopicsRepository topicsRepository) {
        this.topicsRepository = topicsRepository;
    }

    @Override
    protected void showDeviceImpl(DeviceInfo device) {
        loadTopics(device.getTopics().toArray(new String[0]));
        descriptionTextView.setText(device.getDescription());
        idLabel.setText(device.getId());
        Status deviceStatus = device.getStatus();
        statusDateLabel.setText(DeviceViewerUtils.getDate(deviceStatus.getLastChange()));
        statusDateLabel.setPrefixText(DeviceViewerUtils.getConnectionDateStatusStringResource(deviceStatus));
        statusTextView.setText(DeviceViewerUtils.getConnectionStatusStringResource(deviceStatus));
    }

    private void loadTopics(@NonNull String[] topicsIds) {
        if (topicsIds.length == 0)
            return;
        topicsList.removeAllViews();
        topicsRepository.load(data->{
            AndroidUtils.runOnMainThread(() -> {
                LayoutInflater inflater = LayoutInflater.from(topicsList.getContext());
                for (String topicId : topicsIds) {
                    TopicInfo topicInfo = Objects.requireNonNull(data.get(topicId));
                    View view = inflater.inflate(R.layout.fragment_topic_info, topicsList, false);
                    TopicInfoViewer topicInfoViewer = new TopicInfoViewer(view);
                    topicInfoViewer.displayTopic(topicInfo);
                    topicsList.addView(view);
                }
            });
        }, throwable -> {});
//        Database.getInstance()
//                .getTopics(topicsIds, new Database.RequestCallback<Map<String, TopicInfo>>() {
//                    @Override
//                    public void onSuccess(Map<String, TopicInfo> data, int statusCode) {
//                        getActivity().runOnUiThread(() -> {
//                            LayoutInflater inflater = LayoutInflater.from(topicsList.getContext());
//                            for (String topicId : topicsIds) {
//                                TopicInfo topicInfo = Objects.requireNonNull(data.get(topicId));
//                                View view = inflater.inflate(R.layout.fragment_topic_info, topicsList, false);
//                                TopicInfoViewer topicInfoViewer = new TopicInfoViewer(view);
//                                topicInfoViewer.displayTopic(topicInfo);
//                                topicsList.addView(view);
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode) {
//                        Log.d("pttt", "[GET TOPICS] Status code: " + statusCode);
//                    }
//                });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_device_info_view;
    }

    @Override
    protected void findViews(@NonNull View root) {
        idLabel = root.findViewById(R.id.device_info_LBL_id);
        statusDateLabel = root.findViewById(R.id.device_info_LBL_status_date);
        topicsList = root.findViewById(R.id.device_info_LST_topic);
        descriptionTextView = root.findViewById(R.id.device_info_LBL_description);
        statusTextView = root.findViewById(R.id.device_info_LBL_status);
    }
}
