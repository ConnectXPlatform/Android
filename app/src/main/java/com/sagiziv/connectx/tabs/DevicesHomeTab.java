package com.sagiziv.connectx.tabs;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

import com.sagiziv.connectx.DevicesHandler;
import com.sagiziv.connectx.FeedbackHandler;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.activities.DeviceInfoActivity;
import com.sagiziv.connectx.bundlewrappers.DeviceInfoBundleWrapper;
import com.sagiziv.connectx.customviews.Label;
import com.sagiziv.connectx.dataadapters.BaseAdapter;
import com.sagiziv.connectx.dataadapters.DeviceInfoAdapter;
import com.sagiziv.connectx.dto.CreateDeviceDto;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.repositories.DataRepository;
import com.sagiziv.connectx.repositories.DevicesRepository;
import com.sagiziv.connectx.utils.DialogUtils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class DevicesHomeTab extends HomeScreenTab<DeviceInfo> {
    @DrawableRes
    private static final int ICON_RESOURCE = R.drawable.ic_devices;
    private final String thisDeviceId;

    public DevicesHomeTab(String[] ids, AppCompatActivity activity, Consumer<Throwable> onErrorOccurred, String userId, String thisDeviceId) {
        super(ids, activity, onErrorOccurred, userId);
        this.thisDeviceId = thisDeviceId;
        DevicesRepository.initialize(ids, userId);
        DevicesRepository.getInstance().setCallbacks(new DataRepository.RepositoryCallbacks<DeviceInfo>() {

            @Override
            public void onCreated(DeviceInfo deviceInfo) {
                activity.runOnUiThread(() -> {
                    AlertDialog dialog = DialogUtils.createDialog(activity)
                            .withTitle(R.string.device_created_title)
                            .withIcon(ICON_RESOURCE)
                            .withLayoutContent(R.layout.dialog_device_created)
                            .withPositiveButton(R.string.ok_button, Dialog::dismiss)
                            .withNegativeButton("Copy", ignored -> {
                            })
                            .isCancelable(true)
                            .getDialog();
                    dialog.show();
                    dialog.<Label>findViewById(R.id.created_device_LBL_uid)
                            .setText(userId);
                    dialog.<Label>findViewById(R.id.created_device_LBL_did)
                            .setText(deviceInfo.getId());
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(v -> {
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Device credentials",
                                "User id:" + userId + ";Device id:" + deviceInfo.getId());
                        clipboard.setPrimaryClip(clip);
                    });

                    itemAdded(deviceInfo.getId(), deviceInfo);
                });
            }

            @Override
            public void onDeleted(DeviceInfo deviceInfo) {
                activity.runOnUiThread(() -> itemDeleted(deviceInfo.getId(), deviceInfo));
            }
        });
    }

    @Override
    protected BaseAdapter<DeviceInfo> createAdapter(AppCompatActivity activity) {
        return new DeviceInfoAdapter(activity);
    }

    @Override
    protected void loadDataImpl(Runnable onLoadingEnded) {
        DevicesRepository.getInstance()
                .load(data -> {
                    activity.runOnUiThread(() -> addItems(data));
                    onLoadingEnded.run();
//                    DevicesHandler.initialize();
                    DevicesHandler.getInstance()
                            .setOnDeviceConnected(this::deviceConnected);
                    DevicesHandler.getInstance()
                            .setOnDeviceDisconnected(this::deviceDisconnected);
                }, onErrorOccurred);
    }

    private void deviceConnected(String deviceId) {
        DevicesRepository.getInstance()
                .get(deviceId, deviceInfo -> {
                    activity.runOnUiThread(() -> {
                        FeedbackHandler.getInstance()
                                .toast("Device \"" + deviceInfo.getName() + "\" connected");
                        if (newItem(deviceInfo.getId())) {
                            itemAdded(deviceInfo.getId(), deviceInfo);
                        } else {
                            itemUpdated(deviceInfo.getId(), deviceInfo);
                        }
                    });
                }, onErrorOccurred);
    }

    private void deviceDisconnected(String deviceId) {
        DevicesRepository.getInstance()
                .get(deviceId, deviceInfo -> {
                    activity.runOnUiThread(() -> {
                        FeedbackHandler.getInstance()
                                .toast("Device \"" + deviceInfo.getName() + "\" disconnected");
                        itemUpdated(deviceInfo.getId(), deviceInfo);
                    });
                }, onErrorOccurred);
    }

    @Override
    protected Predicate<DeviceInfo> getItemDeletionPredicateImpl() {
        return this::canDeleteDevice;
    }

    private boolean canDeleteDevice(DeviceInfo deviceInfo) {
        return !deviceInfo.getId().equals(thisDeviceId);
    }

    @Override
    protected int getIcon() {
        return ICON_RESOURCE;
    }

    @Override
    protected void addImpl() {
        DialogUtils.createDialog(activity)
                .withTitle(R.string.device_creation_message)
                .withLayoutContent(R.layout.dialog_create_device)
                .withIcon(ICON_RESOURCE)
                .withPositiveButton(R.string.create_button, dialog -> {
                    String name = dialog.<EditText>findViewById(R.id.create_device_INP_name)
                            .getText().toString();
                    String description = dialog.<EditText>findViewById(R.id.create_device_INP_description)
                            .getText().toString();
                    createDevice(name, description);
                })
                .withNegativeButton(R.string.cancel_button, Dialog::dismiss)
                .isCancelable(true)
                .showDialog();
    }

    @Override
    protected Intent getSelectionIntent(DeviceInfo selected) {
        Intent intent = new Intent(activity, DeviceInfoActivity.class);
        DeviceInfoBundleWrapper wrapper = createBundleWrapper(selected);
        wrapper.setIsInEditMode(false);
        intent.putExtras(wrapper.getBundle());
        return intent;
    }

    @Override
    protected Intent getEditIntent(DeviceInfo selected) {
        Intent intent = new Intent(activity, DeviceInfoActivity.class);
        DeviceInfoBundleWrapper wrapper = createBundleWrapper(selected);
        wrapper.setIsInEditMode(true);
        intent.putExtras(wrapper.getBundle());
        return intent;
    }

    private DeviceInfoBundleWrapper createBundleWrapper(DeviceInfo device) {
        DeviceInfoBundleWrapper wrapper = new DeviceInfoBundleWrapper(new Bundle());
        wrapper.setDevice(device);
        wrapper.setDeletable(canDeleteDevice(device));
        return wrapper;
    }

    @Override
    protected ActivityResultLauncher<Intent> createActivityLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Process the result from the second activity
                        Intent data = result.getData();
                        if (data == null) return;
                        DeviceInfoBundleWrapper wrapper = new DeviceInfoBundleWrapper(data.getExtras());
                        DeviceInfo device = wrapper.getDevice();
                        itemUpdated(device.getId(), device);
                    } else if (result.getResultCode() == RESULT_CANCELED) {
                        // Handle if the user canceled the second activity
                    }
                    DevicesHandler.getInstance()
                            .setOnDeviceConnected(this::deviceConnected);
                    DevicesHandler.getInstance()
                            .setOnDeviceDisconnected(this::deviceDisconnected);
                });
    }

    @Override
    protected void deleteImpl(DeviceInfo selected) {
        DevicesRepository.getInstance()
                .delete(selected, null, throwable -> {
                    showErrorAlert("Error communicating with the server:\n" + throwable.getMessage(),
                            ignored -> deleteImpl(selected));
                });
//        String deviceId = selected.getId();
//        Database.getInstance().deleteDevice(deviceId, userId, new Database.EmptyRequestCallback() {
//            @Override
//            public void onSuccess(int statusCode) {
//                activity.runOnUiThread(() -> itemDeleted(deviceId, selected, onItemDeleted));
//            }
//
//            @Override
//            public void onFailure(Throwable throwable) {
//                showErrorAlert("Error communicating with the server:\n" + throwable.getMessage(),
//                        ignored -> deleteImpl(selected, onItemDeleted));
////                activity.runOnUiThread(() -> {
////                    DialogUtils.createDialog(activity)
////                            .withTitle("Error")
////                            .withIcon(R.drawable.ic_error)
////                            .withTextContent("Error communicating with the server:\n" + throwable.getMessage())
////                            .isCancelable(true)
////                            .withPositiveButton("Retry", ignored -> deleteImpl(selected, onItemDeleted))
////                            .showDialog();
////                });
//            }
//
//            @Override
//            public void onFailure(int statusCode) {
//                FeedbackHandler.getInstance().toast("Server returned status code: " + statusCode);
//            }
//        });
    }

    private void createDevice(String name, String description) {
        CreateDeviceDto createDeviceDto = new CreateDeviceDto()
                .setName(name)
                .setDescription(description);
        DevicesRepository.getInstance()
                .add(createDeviceDto, null, onErrorOccurred);
//        Database.getInstance()
//                .createDevice(createDeviceDto, userId, new Database.RequestCallback<DeviceInfo>() {
//                    @Override
//                    public void onSuccess(DeviceInfo deviceInfo, int statusCode) {
//                        activity.runOnUiThread(() -> {
//                            AlertDialog dialog = DialogUtils.createDialog(activity)
//                                    .withTitle(R.string.device_created_title)
//                                    .withIcon(ICON_RESOURCE)
//                                    .withLayoutContent(R.layout.dialog_device_created)
//                                    .withPositiveButton(R.string.ok_button, Dialog::dismiss)
//                                    .isCancelable(true)
//                                    .getDialog();
//
//                            dialog.show();
//                            dialog.<Label>findViewById(R.id.created_device_LBL_uid)
//                                    .setText(userId);
//                            dialog.<Label>findViewById(R.id.created_device_LBL_did)
//                                    .setText(deviceInfo.getId());
//
//                            itemAdded(deviceInfo.getId(), deviceInfo, onItemAdded);
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
