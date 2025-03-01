package com.sagiziv.connectx.dataadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagiziv.connectx.customviews.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter<T>.ViewHolder> {
    private final int itemLayoutId;
    private final LayoutInflater layoutInflater;
    private final List<T> data;
    private ItemSelectedCallback<T> itemSelectedCallback;

    public BaseAdapter(@LayoutRes int itemLayoutId, Context context) {
        super();
        this.itemLayoutId = itemLayoutId;
        this.layoutInflater = LayoutInflater.from(context);
        this.data = new ArrayList<>();
    }

    public BaseAdapter(@LayoutRes int itemLayoutId, Context context, Collection<T> initialData) {
        this(itemLayoutId, context);
        data.addAll(initialData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(itemLayoutId, parent, false);
        return createViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.updateData(data.get(position));
    }

    public void addItem(T item) {
        data.add(item);
        notifyItemInserted(data.size() - 1);
    }

    public void removeItem(T item) {
        int index = data.indexOf(item);
        data.remove(index);
        notifyItemRemoved(index);
    }

    public void insertItem(int pos, T item) {
        data.add(pos, item);
        notifyItemInserted(pos);
    }

    public void clear(){
        notifyItemRangeRemoved(0, data.size());
        data.clear();
    }

    @SafeVarargs
    public final void addItems(T... items) {
        addItems(Arrays.asList(items));
    }

    public void addItems(Collection<T> items) {
        data.addAll(items);
        notifyItemRangeInserted(data.size() - items.size(), items.size());
    }

    public void setItemSelectedCallback(ItemSelectedCallback<T> itemSelectedCallback) {
        this.itemSelectedCallback = itemSelectedCallback;
    }

    protected abstract ViewHolder createViewHolder(View view);

    public T getItem(int position) {
        return data.get(position);
    }

    public void updateItem(T item) {
        int position = data.indexOf(item);
        if (position == -1) return;
        data.set(position, item);
        notifyItemChanged(position);
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews(itemView);
            itemView.setOnClickListener(this);
        }

        public final void updateData(T data) {
            showData(data);
        }

        protected abstract void showData(T data);

        protected abstract void findViews(View itemView);

        @Override
        public void onClick(View v) {
            if (itemSelectedCallback != null)
                itemSelectedCallback.onItemSelected(getAdapterPosition());
        }
    }

    public interface ItemSelectedCallback<T> {
        void onItemSelected(int position);
    }
}
