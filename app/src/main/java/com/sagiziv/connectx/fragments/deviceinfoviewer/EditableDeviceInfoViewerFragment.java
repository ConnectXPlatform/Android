package com.sagiziv.connectx.fragments.deviceinfoviewer;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.sagiziv.connectx.utils.AndroidUtils;
import com.sagiziv.connectx.EditableTopicInfoViewer;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dialogs.TopicEditDialog;
import com.sagiziv.connectx.dto.CreateTopicDto;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.TopicInfo;
import com.sagiziv.connectx.dto.UpdateDeviceDto;
import com.sagiziv.connectx.dto.UpdateTopicDto;
import com.sagiziv.connectx.repositories.DevicesRepository;
import com.sagiziv.connectx.repositories.TopicsRepository;
import com.sagiziv.connectx.utils.DialogUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class EditableDeviceInfoViewerFragment extends AbstractDeviceInfoViewerFragment {
    private LinearLayoutCompat topicsList;
    private EditText nameInputField, descriptionInputField;
    private View newTopicButton;
    private DeviceInfo editedDevice;
    private final TopicsRepository topicsRepository;
    private final Map<String, TopicModification> topicModificationsByTopic = new HashMap<>();
    private final Map<String, EditableTopicInfoViewer> topicViewsByTopic = new HashMap<>();

    public EditableDeviceInfoViewerFragment(TopicsRepository topicsRepository) {
        this.topicsRepository = topicsRepository;
    }

    @Override
    protected void showDeviceImpl(DeviceInfo device) {
        editedDevice = device;
        loadTopics(device.getTopics().toArray(new String[0]));
        nameInputField.setText(device.getName());
        descriptionInputField.setText(device.getDescription());
    }

    private void loadTopics(String[] topicsIds) {
        if (topicsIds.length == 0)
            return;
        topicsRepository.load(data -> {
            AndroidUtils.runOnMainThread(() -> {
                LayoutInflater inflater = LayoutInflater.from(topicsList.getContext());
                for (String topicId : topicsIds) {
                    createTopicInfoView(Objects.requireNonNull(data.get(topicId)), inflater);
                }
            });
        }, throwable -> {

        });
    }

    private void createTopicInfoView(TopicInfo topicInfo, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_editable_topic_info, topicsList, false);
        EditableTopicInfoViewer topicInfoViewer = new EditableTopicInfoViewer(view);
        topicInfoViewer.displayTopic(topicInfo);
        topicInfoViewer.setOnDelete(this::deleteTopic);
        topicInfoViewer.setOnClick(this::updateTopic);
        topicsList.addView(view);
        topicViewsByTopic.put(topicInfo.getId(), topicInfoViewer);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_editable_device_info_view;
    }

    @Override
    protected void findViews(@NonNull View root) {
        topicsList = root.findViewById(R.id.device_info_LST_topic);
        nameInputField = root.findViewById(R.id.device_info_INP_name);
        descriptionInputField = root.findViewById(R.id.device_info_INP_description);
        newTopicButton = root.findViewById(R.id.device_info_BTN_new_topic);
    }

    @Override
    protected void initializeViews() {
        final TopicEditDialog topicEditDialog = new TopicEditDialog();
        newTopicButton.setOnClickListener(v -> {
            AlertDialog dialog = DialogUtils.createDialog(getContext())
                    .withTitle("New topic")
                    .withLayoutContent(topicEditDialog.getLayoutResource())
                    .isCancelable(true)
                    .withPositiveButton(R.string.create_button, ignored -> createTopic(topicEditDialog.create()))
                    .getDialog();
            dialog.setOnShowListener(topicEditDialog::initializeViews);
            dialog.show();
        });
    }

    private void deleteTopic(TopicInfo topicInfo) {
        topicsList.removeView(topicViewsByTopic.get(topicInfo.getId()).getRootView());
        TopicModification current = topicModificationsByTopic.get(topicInfo.getId());
        // If this is one of the new topics we created, delete it as it is not needed anymore.
        if (current instanceof NewTopicTopicModification) {
            topicModificationsByTopic.remove(topicInfo.getId());
            return;
        }
        topicModificationsByTopic.put(topicInfo.getId(), new TopicDeletionTopicModification(topicInfo, editedDevice));
    }

    private void updateTopic(TopicInfo topicInfo) {
        final TopicEditDialog topicEditDialog = new TopicEditDialog(topicInfo);
        AlertDialog dialog = DialogUtils.createDialog(getContext())
                .withTitle("New topic")
                .withLayoutContent(topicEditDialog.getLayoutResource())
                .isCancelable(true)
                .withPositiveButton("Save", ignored -> {
                    UpdateTopicDto update = topicEditDialog.update();
                    if (!update.hasUpdate()) return;
                    if (update.getTopic() != null)
                        topicInfo.setTopic(update.getTopic());
                    if (update.getDataType() != null)
                        topicInfo.setDataType(update.getDataType());
                    if (update.getModeFlags() != null)
                        topicInfo.setModeFlags(update.getModeFlags());

                    topicViewsByTopic.get(topicInfo.getId()).displayTopic(topicInfo);
                    TopicModification currentModification = topicModificationsByTopic.get(topicInfo.getId());
                    if (currentModification != null)
                        currentModification.merge(update);
                    else
                        topicModificationsByTopic.put(topicInfo.getId(),
                                new TopicUpdateTopicModification(update, topicInfo.getId()));

                })
                .getDialog();
        dialog.setOnShowListener(topicEditDialog::initializeViews);
        dialog.show();
    }

    private void createTopic(@NonNull CreateTopicDto createTopicDto) {
        // Add a temporary view for the topic with a temporary id.
        String id = generateId();
        topicModificationsByTopic.put(id, new NewTopicTopicModification(createTopicDto, editedDevice));
        LayoutInflater inflater = LayoutInflater.from(topicsList.getContext());
        createTopicInfoView(new TopicInfo(id, createTopicDto.getTopic(),
                createTopicDto.getModeFlags(), createTopicDto.getDataType()), inflater);
    }

    public void applyChanges(Runnable onSuccess, Consumer<Throwable> onError) {
        applyChanges(new ArrayList<>(topicModificationsByTopic.values()), 0,
                () -> {
                    UpdateDeviceDto updateDevice = new UpdateDeviceDto();
                    if (!nameInputField.getText().toString().equals(editedDevice.getName())) {
                        updateDevice.setName(nameInputField.getText().toString());
                        editedDevice.setName(nameInputField.getText().toString());
                    }
                    if (!descriptionInputField.getText().toString().equals(editedDevice.getDescription())) {
                        updateDevice.setDescription(descriptionInputField.getText().toString());
                        editedDevice.setDescription(descriptionInputField.getText().toString());
                    }
                    if (updateDevice.hasUpdate()) {
                        DevicesRepository.getInstance()
                                .update(updateDevice, editedDevice.getId(),
                                        () -> AndroidUtils.runOnMainThread(onSuccess), onError);
                    } else {
                        AndroidUtils.runOnMainThread(onSuccess);
                    }
                }, onError);
    }

    private void applyChanges(List<TopicModification> modifications, int index, Runnable onSuccess, Consumer<Throwable> onError) {
        if (index == modifications.size()) {
            onSuccess.run();
            return;
        }
        modifications.get(index).apply(() -> {
            applyChanges(modifications, index + 1, onSuccess, onError);
        }, onError);
    }

    private interface TopicModification {
        void apply(Runnable onSuccess, Consumer<Throwable> onFailure);

        void merge(UpdateTopicDto update);
    }

    private final class NewTopicTopicModification implements TopicModification {
        private final CreateTopicDto createTopicDto;
        private final DeviceInfo parentDevice;

        public NewTopicTopicModification(CreateTopicDto createTopicDto, DeviceInfo parentDevice) {
            this.createTopicDto = createTopicDto;
            this.parentDevice = parentDevice;
        }

        @Override
        public void apply(Runnable onSuccess, Consumer<Throwable> onFailure) {
            topicsRepository.add(createTopicDto, data -> {
                parentDevice.getTopics().add(data.getId());
                onSuccess.run();
            }, onFailure);
        }

        @Override
        public void merge(UpdateTopicDto update) {
            if (update.getTopic() != null)
                createTopicDto.setTopic(update.getTopic());
            if (update.getModeFlags() != null)
                createTopicDto.setModeFlags(update.getModeFlags());
            if (update.getDataType() != null)
                createTopicDto.setDataType(update.getDataType());
        }
    }

    private final class TopicUpdateTopicModification implements TopicModification {
        private final UpdateTopicDto updateTopicDto;
        private final String topicId;

        public TopicUpdateTopicModification(UpdateTopicDto updateTopicDto, String topicId) {
            this.updateTopicDto = updateTopicDto;
            this.topicId = topicId;
        }

        @Override
        public void apply(Runnable onSuccess, Consumer<Throwable> onFailure) {
            topicsRepository.update(updateTopicDto, topicId, onSuccess, onFailure);
        }

        @Override
        public void merge(UpdateTopicDto update) {
            if (update.getTopic() != null)
                updateTopicDto.setTopic(update.getTopic());
            if (update.getModeFlags() != null)
                updateTopicDto.setModeFlags(update.getModeFlags());
            if (update.getDataType() != null)
                updateTopicDto.setDataType(update.getDataType());
        }
    }

    private final class TopicDeletionTopicModification implements TopicModification {

        private final TopicInfo topic;
        private final DeviceInfo parentDevice;

        private TopicDeletionTopicModification(TopicInfo topic, DeviceInfo parentDevice) {
            this.topic = topic;
            this.parentDevice = parentDevice;
        }

        @Override
        public void apply(Runnable onSuccess, Consumer<Throwable> onFailure) {
            topicsRepository.delete(topic, () -> {
                parentDevice.getTopics().remove(topic.getId());
                onSuccess.run();
            }, onFailure);
        }

        @Override
        public void merge(UpdateTopicDto update) {
        }
    }

    @NonNull
    private static String generateId() {
        final UUID uuid = UUID.randomUUID();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        final byte[] bytes = byteBuffer.array();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
