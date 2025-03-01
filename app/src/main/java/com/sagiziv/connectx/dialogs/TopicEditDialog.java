package com.sagiziv.connectx.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.CreateTopicDto;
import com.sagiziv.connectx.dto.TopicInfo;
import com.sagiziv.connectx.dto.UpdateTopicDto;
import com.sagiziv.connectx.textwatchers.EmptyTextWatcher;
import com.sagiziv.connectx.utils.ColorUtils;

import java.util.Locale;

public final class TopicEditDialog {
    private final String[] topicCategories = {"Primitive", "Sensor"};
    private final String[][] subCategories = {{"int", "boolean"}, {"temperature"}};

    private EditText topicText;
    private AutoCompleteTextView mainCategoryTextView;
    private AutoCompleteTextView subCategoryTextView;
    private CheckBox dataProviderCheckBox;
    private CheckBox commandsProcessorCheckBox;
    private Button dialogPositiveButton;
    private ValidationState validationState;
    @Nullable
    private final TopicInfo editedTopic;

    public TopicEditDialog() {
        this.editedTopic = null;
    }

    public TopicEditDialog(@Nullable TopicInfo editedTopic) {
        this.editedTopic = editedTopic;
    }

    @LayoutRes
    public int getLayoutResource() {
        return R.layout.dialog_edit_topic;
    }

    public CreateTopicDto create() {
        return new CreateTopicDto()
                .setTopic(topicText.getText().toString())
                .setDataType((mainCategoryTextView.getText().toString() + "." +
                        subCategoryTextView.getText().toString()).toLowerCase(Locale.getDefault()))
                .setModeFlags(TopicInfo.getTopicModeFlags(dataProviderCheckBox.isChecked(),
                        commandsProcessorCheckBox.isChecked()));
    }

    public UpdateTopicDto update() {
        if (editedTopic == null)
            throw new RuntimeException("Trying to update non-existing topic!");
        UpdateTopicDto update = new UpdateTopicDto();

        if (!editedTopic.getTopic().equals(topicText.getText().toString()))
            update.setTopic(topicText.getText().toString());
        String dataType = (mainCategoryTextView.getText().toString() + "." +
                subCategoryTextView.getText().toString()).toLowerCase(Locale.getDefault());
        if (!editedTopic.getDataType().equals(dataType))
            update.setDataType(dataType);
        int modeFlags = TopicInfo.getTopicModeFlags(dataProviderCheckBox.isChecked(),
                commandsProcessorCheckBox.isChecked());
        if (editedTopic.getModeFlags() != modeFlags)
            update.setModeFlags(modeFlags);

        return update;
    }

    public void initializeViews(DialogInterface dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;
        dialogPositiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        topicText = dialog.findViewById(R.id.edit_topic_INP_topic);
        mainCategoryTextView = dialog.findViewById(R.id.edit_topic_INP_type_main_category);
        subCategoryTextView = dialog.findViewById(R.id.edit_topic_INP_type_sub_category);
        dataProviderCheckBox = dialog.findViewById(R.id.edit_topic_CHK_data_provider);
        commandsProcessorCheckBox = dialog.findViewById(R.id.edit_topic_CHK_commands_processor);

        addValidation();
        if (editedTopic != null) {
            topicText.setText(editedTopic.getTopic());
            String[] categories = editedTopic.getDataType().split("\\.");
            mainCategoryTextView.setText(categories[0]);
            subCategoryTextView.setText(categories[1]);
            dataProviderCheckBox.setChecked(editedTopic.isDataProvider());
            commandsProcessorCheckBox.setChecked(editedTopic.isCommandsProcessor());
        }
        initializeDropdowns(dialog);
    }

    private void initializeDropdowns(@NonNull AlertDialog dialog) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line);
        adapter.addAll(topicCategories);

        mainCategoryTextView.setAdapter(adapter);
        mainCategoryTextView.setOnItemClickListener((parent, view, position, id) -> {
            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line);
            subAdapter.addAll(subCategories[position]);
            subCategoryTextView.setText("");
            subCategoryTextView.setAdapter(subAdapter);
        });

        if (mainCategoryTextView.getText().length() > 0) {
            String value = mainCategoryTextView.getText().toString();
            int index = 0;
            for (int i = 0; i < topicCategories.length; i++) {
                if (topicCategories[i].equalsIgnoreCase(value)) {
                    index = i;
                    break;
                }
            }
            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line);
            subAdapter.addAll(subCategories[index]);
            subCategoryTextView.setAdapter(subAdapter);
        }
    }

    private void addValidation() {
        validationState = new ValidationState();
        disableDialogButton();

        registerValidationListeners();
        invokeValidationListeners();
    }

    private void registerValidationListeners() {
        topicText.addTextChangedListener(new EmptyTextWatcher(topicText, validationState::setTopicValid));
        mainCategoryTextView.addTextChangedListener(new EmptyTextWatcher(mainCategoryTextView, validationState::setMainCategoryValid));
        subCategoryTextView.addTextChangedListener(new EmptyTextWatcher(subCategoryTextView, validationState::setSubCategoryValid));
        dataProviderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> validationState.setDataProviderChecked(isChecked));
        commandsProcessorCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> validationState.setCommandsProcessorChecked(isChecked));
    }

    private void invokeValidationListeners() {
        topicText.setText("");
        mainCategoryTextView.setText("");
        subCategoryTextView.setText("");
        dataProviderCheckBox.setChecked(true);
    }

    private void disableDialogButton() {
        dialogPositiveButton.setTextColor(ColorUtils.getColor(dialogPositiveButton.getContext(), R.color.grey));
        dialogPositiveButton.setEnabled(false);
    }

    private void enableDialogButton() {
        dialogPositiveButton.setTextColor(ColorUtils.getColor(dialogPositiveButton.getContext(), R.color.white));
        dialogPositiveButton.setEnabled(true);
    }

    private final class ValidationState {
        private boolean topicValid, mainCategoryValid, subCategoryValid;
        private boolean dataProviderChecked, commandsProcessorChecked;

        public void setTopicValid(boolean topicValid) {
            this.topicValid = topicValid;
            updateButton();
        }

        public void setMainCategoryValid(boolean mainCategoryValid) {
            this.mainCategoryValid = mainCategoryValid;
            updateButton();
        }

        public void setSubCategoryValid(boolean subCategoryValid) {
            this.subCategoryValid = subCategoryValid;
            updateButton();
        }

        public void setDataProviderChecked(boolean dataProviderChecked) {
            this.dataProviderChecked = dataProviderChecked;
            updateButton();
        }

        public void setCommandsProcessorChecked(boolean commandsProcessorChecked) {
            this.commandsProcessorChecked = commandsProcessorChecked;
            updateButton();
        }

        private void updateButton() {
            if (topicValid && mainCategoryValid && subCategoryValid &&
                    (dataProviderChecked || commandsProcessorChecked))
                enableDialogButton();
            else
                disableDialogButton();
        }
    }
}
