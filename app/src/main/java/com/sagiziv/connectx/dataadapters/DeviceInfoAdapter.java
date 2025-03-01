package com.sagiziv.connectx.dataadapters;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.fragments.DeviceInfoCarouselViewer;

import java.util.Collection;

public class DeviceInfoAdapter extends ViewPagerAdapter<DeviceInfo> {
    public DeviceInfoAdapter(Context context) {
        super(R.layout.device_carousel_view, context);
    }

    public DeviceInfoAdapter(Context context, Collection<DeviceInfo> initialData) {
        super(R.layout.device_carousel_view, context, initialData);
    }

    @Override
    protected BaseAdapter<DeviceInfo>.ViewHolder createViewHolder(View view) {
        return new DeviceInfoHolder(view);
    }

    private class DeviceInfoHolder extends ViewPagerAdapter<DeviceInfo>.Pager2ViewHolder {

        private final DeviceInfoCarouselViewer deviceInfoCarouselViewer;

        public DeviceInfoHolder(@NonNull View itemView) {
            super(itemView);
            deviceInfoCarouselViewer = new DeviceInfoCarouselViewer(itemView);
            itemView.findViewById(R.id.device_carousel_LAY_root).setOnClickListener(this);
        }

        @Override
        protected void showData(DeviceInfo data) {
            deviceInfoCarouselViewer.showDevice(data);
        }
    }
}
