package com.sagiziv.connectx.fragments.deviceinfoviewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sagiziv.connectx.dto.DeviceInfo;

public abstract class AbstractDeviceInfoViewerFragment extends Fragment {

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutResource(), container, false);
        findViews(v);
        initializeViews();
        return v;
    }

    public final void showDevice(DeviceInfo device) {
        showDeviceImpl(device);
    }

    protected void initializeViews(){}
    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract void findViews(View root);
    protected abstract void showDeviceImpl(DeviceInfo device);

}
