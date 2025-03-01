package com.sagiziv.connectx.dataadapters;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.ControlPanel;
import com.sagiziv.connectx.fragments.ControlPanelViewer;

import java.util.Collection;

public class ControlPanelAdapter extends ViewPagerAdapter<ControlPanel> {
    public ControlPanelAdapter(Context context) {
        super(R.layout.control_panel_carousel_view, context);
    }

    public ControlPanelAdapter(Context context, Collection<ControlPanel> initialData) {
        super(R.layout.control_panel_carousel_view, context, initialData);
    }

    @Override
    protected BaseAdapter<ControlPanel>.ViewHolder createViewHolder(View view) {
        return new ControlPanelHolder(view);
    }

    private class ControlPanelHolder extends ViewPagerAdapter<ControlPanel>.Pager2ViewHolder {

        private final ControlPanelViewer controlPanelViewer;

        public ControlPanelHolder(@NonNull View itemView) {
            super(itemView);
            controlPanelViewer = new ControlPanelViewer(itemView);
            itemView.findViewById(R.id.control_panel_carousel_LAY_root).setOnClickListener(this);
        }

        @Override
        protected void showData(ControlPanel data) {
            controlPanelViewer.showControlPanel(data);
        }
    }
}
