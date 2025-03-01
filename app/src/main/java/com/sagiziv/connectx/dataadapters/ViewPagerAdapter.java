package com.sagiziv.connectx.dataadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.DeviceInfo;

import java.util.Collection;

public class ViewPagerAdapter<T> extends BaseAdapter<T> {

    public ViewPagerAdapter(int itemLayoutId, Context context) {
        super(itemLayoutId, context);
    }

    public ViewPagerAdapter(int itemLayoutId, Context context, Collection<T> initialData) {
        super(itemLayoutId, context, initialData);
    }

    @Override
    protected BaseAdapter<T>.ViewHolder createViewHolder(View view) {
        return new Pager2ViewHolder(view);
    }

    public class Pager2ViewHolder extends ViewHolder {
        private final View view;

        public Pager2ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }

        @Override
        protected void showData(T data) {

        }

        @Override
        protected void findViews(View itemView) {

        }

        public View getView(){
            return view;
        }
    }
}
