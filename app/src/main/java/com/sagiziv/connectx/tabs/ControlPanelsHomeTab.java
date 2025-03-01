package com.sagiziv.connectx.tabs;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.activities.ControlPanelViewActivity;
import com.sagiziv.connectx.bundlewrappers.ControlPanelBundleWrapper;
import com.sagiziv.connectx.dataadapters.BaseAdapter;
import com.sagiziv.connectx.dataadapters.ControlPanelAdapter;
import com.sagiziv.connectx.dto.ControlPanel;
import com.sagiziv.connectx.dto.CreateControlPanelDto;
import com.sagiziv.connectx.repositories.ControlPanelsRepository;
import com.sagiziv.connectx.repositories.DataRepository;
import com.sagiziv.connectx.utils.DialogUtils;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ControlPanelsHomeTab extends HomeScreenTab<ControlPanel> {
    @DrawableRes
    private static final int ICON_RESOURCE = R.drawable.ic_collection;

    public ControlPanelsHomeTab(String[] ids, AppCompatActivity activity, Consumer<Throwable> onErrorOccurred, String userId) {
        super(ids, activity, onErrorOccurred, userId);
        ControlPanelsRepository.initialize(ids, userId);
        ControlPanelsRepository.getInstance()
                .setCallbacks(new DataRepository.RepositoryCallbacks<ControlPanel>() {
                    @Override
                    public void onCreated(ControlPanel controlPanel) {
                        activity.runOnUiThread(() -> {
                            DialogUtils.createDialog(activity)
                                    .withTitle("Created control panel")
                                    .withIcon(ICON_RESOURCE)
                                    .withTextContent("Control panel was created successfully")
                                    .withPositiveButton("Done", Dialog::dismiss)
                                    .isCancelable(true)
                                    .showDialog();
                            itemAdded(controlPanel.getId(), controlPanel);
                        });
                    }

                    @Override
                    public void onDeleted(ControlPanel controlPanel) {
                        activity.runOnUiThread(() -> itemDeleted(controlPanel.getId(), controlPanel));
                    }
                });
    }


    @Override
    protected BaseAdapter<ControlPanel> createAdapter(AppCompatActivity activity) {
        return new ControlPanelAdapter(activity);
    }

    @Override
    protected void loadDataImpl(Runnable onLoadingEnded) {
        ControlPanelsRepository.getInstance()
                .load(data -> {
                    activity.runOnUiThread(() -> addItems(data));
                    onLoadingEnded.run();
                }, onErrorOccurred);
    }

    @Override
    protected Predicate<ControlPanel> getItemDeletionPredicateImpl() {
        return ignored -> true;
    }

    @Override
    protected int getIcon() {
        return ICON_RESOURCE;
    }

    @Override
    protected void addImpl() {
        DialogUtils.createDialog(activity)
                .withTitle("Create new control panel")
                .withLayoutContent(R.layout.dialog_create_control_panel)
                .withIcon(ICON_RESOURCE)
                .withPositiveButton(R.string.create_button, dialog -> {
                    String name = dialog.<EditText>findViewById(R.id.create_control_panel_INP_name)
                            .getText().toString();
                    String description = dialog.<EditText>findViewById(R.id.create_control_panel_INP_description)
                            .getText().toString();
                    createControlPanel(name, description);
                })
                .withNegativeButton(R.string.cancel_button, Dialog::dismiss)
                .isCancelable(true)
                .showDialog();
    }

    @Override
    protected Intent getSelectionIntent(ControlPanel selected) {
        Intent intent = new Intent(activity, ControlPanelViewActivity.class);
        ControlPanelBundleWrapper wrapper = new ControlPanelBundleWrapper(new Bundle());
        wrapper.setControlPanel(selected);
        wrapper.setIsInEditMode(false);
        intent.putExtras(wrapper.getBundle());
        return intent;
    }

    @Override
    protected Intent getEditIntent(ControlPanel selected) {
        Intent intent = new Intent(activity, ControlPanelViewActivity.class);
        ControlPanelBundleWrapper wrapper = new ControlPanelBundleWrapper(new Bundle());
        wrapper.setControlPanel(selected);
        wrapper.setIsInEditMode(true);
        intent.putExtras(wrapper.getBundle());
        return intent;
    }

    @Override
    protected void deleteImpl(@NonNull ControlPanel selected) {
        ControlPanelsRepository.getInstance()
                .delete(selected, null, throwable -> {
                    showErrorAlert("Error communicating with the server:\n" + throwable.getMessage(),
                            ignored -> deleteImpl(selected));
                });
    }

    @Override
    protected ActivityResultLauncher<Intent> createActivityLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Process the result from the second activity
                        Intent data = result.getData();
                        if (data == null)return;
                        ControlPanelBundleWrapper wrapper = new ControlPanelBundleWrapper(data.getExtras());
                        ControlPanel controlPanel = wrapper.getControlPanel();
                        itemUpdated(controlPanel.getId(), controlPanel);
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        // Handle if the user canceled the second activity
                    }
                });
    }

    private void createControlPanel(String name, String description) {
        CreateControlPanelDto createControlPanelDto = new CreateControlPanelDto()
                .setName(name)
                .setDescription(description)
                .setCreator(userId);
        ControlPanelsRepository.getInstance()
                .add(createControlPanelDto, null, onErrorOccurred);
//        Database.getInstance()
//                .createControlPanel(createControlPanelDto, userId, new Database.RequestCallback<ControlPanel>() {
//                    @Override
//                    public void onSuccess(ControlPanel controlPanel, int statusCode) {
//                        activity.runOnUiThread(() -> {
//                            DialogUtils.createDialog(activity)
//                                    .withTitle("Created control panel")
//                                    .withIcon(ICON_RESOURCE)
//                                    .withTextContent("Control panel was created successfully")
//                                    .withPositiveButton("Done", Dialog::dismiss)
//                                    .isCancelable(true)
//                                    .showDialog();
//
//                            itemAdded(controlPanel.getId(), controlPanel);
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
//
//                    }
//                });
    }
}
