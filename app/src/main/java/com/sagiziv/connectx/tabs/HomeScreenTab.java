package com.sagiziv.connectx.tabs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dataadapters.BaseAdapter;
import com.sagiziv.connectx.utils.DialogUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class HomeScreenTab<T> {

    private final List<String> ids;
    private final Map<String, T> idsMap;
    private final BaseAdapter<T> adapter;
    private BiConsumer<BaseAdapter<T>, Integer> onItemDeleted, onItemAdded;
    protected final Consumer<Throwable> onErrorOccurred;
    protected final AppCompatActivity activity;
    protected final String userId;
    private final ActivityResultLauncher<Intent> launcher;

    public HomeScreenTab(String[] ids, AppCompatActivity activity, Consumer<Throwable> onErrorOccurred, String userId) {
        this.ids = Arrays.stream(ids).collect(Collectors.toList());
        this.activity = activity;
//        this.onAdapterChanged = onAdapterChanged;
        this.onErrorOccurred = onErrorOccurred;
        this.userId = userId;

        this.idsMap = new HashMap<>(ids.length);
        this.adapter = createAdapter(activity);
        this.adapter.setItemSelectedCallback(this::select);
        this.launcher = createActivityLauncher();
    }

    protected abstract BaseAdapter<T> createAdapter(AppCompatActivity activity);

    public BaseAdapter<T> getAdapter() {
        return adapter;
    }

    public void loadData(@NonNull Runnable onLoadingEnded) {
        if (ids.isEmpty()) {
            onLoadingEnded.run();
            return;
        }
        loadDataImpl(onLoadingEnded);
    }

    protected abstract void loadDataImpl(Runnable onLoadingEnded);

    public Predicate<Integer> getItemDeletionPredicate() {
        Predicate<Integer> integerPredicate = integer -> !ids.isEmpty();
        return integerPredicate.and(integer -> {
            T item = getSelected(integer);
            return getItemDeletionPredicateImpl().test(item);
        });
    }

    public void setOnItemAdded(BiConsumer<BaseAdapter<T>, Integer> onItemAdded) {
        this.onItemAdded = onItemAdded;
    }

    public void setOnItemDeleted(BiConsumer<BaseAdapter<T>, Integer> onItemDeleted) {
        this.onItemDeleted = onItemDeleted;
    }

    protected abstract Predicate<T> getItemDeletionPredicateImpl();

    public void add() {
        addImpl();
    }

    public void select(int index) {
        launcher.launch(getSelectionIntent(getSelected(index)));
    }

    public void edit(int index) {
        launcher.launch(getEditIntent(getSelected(index)));
    }

    public void delete(int index) {
        DialogUtils.createDialog(activity)
                .withTitle(R.string.delete_confirmation_title)
                .withIcon(getIcon())
                .withTextContent(R.string.delete_confirmation_message)
                .isCancelable(true)
                .withPositiveButton(R.string.confirm_delete, dialog -> {
                    deleteImpl(getSelected(index));
                })
                .withNegativeButton(R.string.cancel_delete, Dialog::dismiss)
                .showDialog();
    }

    @DrawableRes
    protected abstract int getIcon();

    protected abstract void addImpl();

    protected abstract Intent getSelectionIntent(T selected);

    protected abstract Intent getEditIntent(T selected);

    protected abstract void deleteImpl(T selected);

    protected abstract ActivityResultLauncher<Intent> createActivityLauncher();

    protected void addItems(Map<String, T> itemsMap) {
        adapter.addItems(itemsMap.values());
        idsMap.putAll(itemsMap);
    }

    protected void itemAdded(String itemId, T item) {
        adapter.addItem(item);
        idsMap.put(itemId, item);
        ids.add(itemId);
        if (onItemAdded != null)
            onItemAdded.accept(adapter, ids.size() - 1);
    }

    protected void itemUpdated(String itemId, T item) {
        idsMap.put(itemId, item);
        adapter.updateItem(item);
    }

    protected void itemDeleted(String itemId, T item) {
        adapter.removeItem(item);
        idsMap.remove(itemId, item);
        int itemIndex = ids.indexOf(itemId);
        ids.remove(itemIndex);
        if (onItemDeleted != null)
            onItemDeleted.accept(adapter, itemIndex);
    }

    protected void showErrorAlert(String message, Consumer<AlertDialog> retryAction) {
        activity.runOnUiThread(() -> {
            DialogUtils.createDialog(activity)
                    .withTitle(R.string.error_title)
                    .withIcon(R.drawable.ic_error)
                    .withTextContent(message)
                    .isCancelable(true)
                    .withPositiveButton(R.string.retry_button, retryAction)
                    .showDialog();
        });
    }

    protected boolean newItem(String itemId){
        return !ids.contains(itemId);
    }

    private T getSelected(final int index) {
        String id = ids.get(index);
        return idsMap.get(id);
    }
}
