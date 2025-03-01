package com.sagiziv.connectx.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sagiziv.connectx.utils.AndroidUtils;
import com.sagiziv.connectx.DevicesHandler;
import com.sagiziv.connectx.FeedbackHandler;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.bundlewrappers.DeviceInfoBundleWrapper;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.fragments.deviceinfoviewer.AbstractDeviceInfoViewerFragment;
import com.sagiziv.connectx.fragments.deviceinfoviewer.DeviceInfoViewerFragment;
import com.sagiziv.connectx.fragments.deviceinfoviewer.EditableDeviceInfoViewerFragment;
import com.sagiziv.connectx.repositories.DevicesRepository;
import com.sagiziv.connectx.repositories.TopicsRepository;
import com.sagiziv.connectx.utils.ColorUtils;
import com.sagiziv.connectx.utils.DialogUtils;

public class DeviceInfoActivity extends AppCompatActivity {

    private DeviceInfo device;
    private AbstractDeviceInfoViewerFragment currentDeviceViewFragment;
    private Toolbar toolbar;
    private boolean isInEditMode;
    private TopicsRepository topicsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        DeviceInfoBundleWrapper wrapper = new DeviceInfoBundleWrapper(getIntent().getExtras());
        device = wrapper.getDevice();

        Log.d("pttt", "Device id: " + device.getId() + ", name: " + device.getName());

        toolbar = findViewById(R.id.topAppBar);
        toolbar.setTitle(device.getName());
        isInEditMode = wrapper.isInEditMode();
        topicsRepository = new TopicsRepository(device);

        currentDeviceViewFragment = wrapper.isInEditMode()
                ? new EditableDeviceInfoViewerFragment(topicsRepository)
                : new DeviceInfoViewerFragment(topicsRepository);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.device_info_LAY_view, currentDeviceViewFragment)
                .runOnCommit(() -> currentDeviceViewFragment.showDevice(device))
                .commit();
        initializeToolbar(wrapper.isDeletable());
    }

    private void initializeToolbar(boolean deletable) {
        MenuItem deleteMenuItem = toolbar.getMenu().findItem(R.id.menu_delete);
        if (!deletable) {
            Drawable icon = deleteMenuItem.getIcon();
            deleteMenuItem.setIcon(AndroidUtils.tintIcon(icon, ColorUtils.getColor(this, R.color.grey)));
            deleteMenuItem.setOnMenuItemClickListener(v -> {
                FeedbackHandler.getInstance().toast(R.string.cant_delete);
                return true;
            });
        } else {
            deleteMenuItem.setOnMenuItemClickListener(null);
        }

        if (isInEditMode) {
            enterEditMode();
        } else {
            exitEditMode();
        }

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_delete) {
                deleteItem();
                return true;
            }
            if (item.getItemId() == R.id.menu_edit) {
                if (!isInEditMode)
                    enterEditMode();
                else {
                    ((EditableDeviceInfoViewerFragment) currentDeviceViewFragment).applyChanges(this::exitEditMode, throwable -> {
                    });
//                    exitEditMode();
                }
                isInEditMode = !isInEditMode;
                return true;
            }
            return false;
        });
    }

    private void enterEditMode() {
        DevicesHandler.getInstance()
                .removeStateListener(device.getId());
        AbstractDeviceInfoViewerFragment editModeFragment = new EditableDeviceInfoViewerFragment(topicsRepository);
        getSupportFragmentManager().beginTransaction()
                .remove(currentDeviceViewFragment)
                .add(R.id.device_info_LAY_view, editModeFragment)
                .runOnCommit(() -> {
                    currentDeviceViewFragment = editModeFragment;
                    currentDeviceViewFragment.showDevice(device);
                })
                .commit();
        MenuItem editMenuItem = toolbar.getMenu().findItem(R.id.menu_edit);
        editMenuItem.setIcon(R.drawable.ic_save);
        editMenuItem.setTitle("Save");
    }

    private void exitEditMode() {
        AbstractDeviceInfoViewerFragment editModeFragment = new DeviceInfoViewerFragment(topicsRepository);
        getSupportFragmentManager().beginTransaction()
                .remove(currentDeviceViewFragment)
                .add(R.id.device_info_LAY_view, editModeFragment)
                .runOnCommit(() -> {
                    currentDeviceViewFragment = editModeFragment;
                    currentDeviceViewFragment.showDevice(device);
                })
                .commit();
        MenuItem editMenuItem = toolbar.getMenu().findItem(R.id.menu_edit);
        editMenuItem.setIcon(R.drawable.ic_edit);
        editMenuItem.setTitle("Edit");
        toolbar.setTitle(device.getName());
        saveCurrentStateToResult();
        DevicesHandler.getInstance()
                .listenToDeviceStateChanges(device.getId(), this::deviceStateChanged);

    }

    private void saveCurrentStateToResult() {
        // Put the updated device in the result, so the home activity can update accordingly.
        Intent resultIntent = new Intent();
        DeviceInfoBundleWrapper wrapper = new DeviceInfoBundleWrapper(new Bundle());
        wrapper.setDevice(device);
        resultIntent.putExtras(wrapper.getBundle());
        setResult(RESULT_OK, resultIntent);
    }

    private void deleteItem() {
        DialogUtils.createDialog(this)
                .withTitle(R.string.delete_confirmation_title)
                .withIcon(R.drawable.ic_delete)
                .withTextContent(R.string.delete_confirmation_message)
                .isCancelable(true)
                .withPositiveButton(R.string.confirm_delete, dialog -> {
                    DevicesRepository.getInstance().delete(device, this::finish, throwable -> {

                    });
//                    finish(); // This activity is no longer relevant
                })
                .withNegativeButton(R.string.cancel_delete, Dialog::dismiss)
                .showDialog();
    }

    private void deviceStateChanged() {
        DevicesRepository.getInstance()
                .get(device.getId(), deviceInfo -> {
                    runOnUiThread(() -> {
                        saveCurrentStateToResult();
                        currentDeviceViewFragment.showDevice(deviceInfo);
                    });
                }, t -> {
                });
    }

    @Override
    public void onBackPressed() {
        DevicesHandler.getInstance()
                .removeStateListener(device.getId());
        super.onBackPressed();
    }
}