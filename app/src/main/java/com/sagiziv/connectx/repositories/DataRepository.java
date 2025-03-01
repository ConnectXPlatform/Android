package com.sagiziv.connectx.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sagiziv.connectx.Database;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class DataRepository<TDto, TCreateDto, TUpdateDto> {
    private final Map<String, TDto> idsMap;
    private RepositoryCallbacks<TDto> callbacks;
    private final List<String> ids;
    protected final String parentId;

    protected DataRepository(List<String> ids, String parentId) {
        this.ids = ids;
        idsMap = new HashMap<>(ids.size());
        this.parentId = parentId;
    }

    public Collection<TDto> getValues() {
        return idsMap.values();
    }

    public Collection<String> getIds() {
        return ids;
    }

    public void load(Consumer<Map<String, TDto>> onDataLoaded, Consumer<Throwable> onError) {
        if (ids.isEmpty()) {
            onDataLoaded.accept(Collections.emptyMap());
            return;
        }
        loadImpl(new Database.RequestCallback<Map<String, TDto>>() {
            @Override
            public void onSuccess(Map<String, TDto> data, int statusCode) {
                onDataLoaded.accept(data);
                idsMap.putAll(data);
            }

            @Override
            public void onSuccess(int statusCode) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("pttt", "Error while loading " + getClass().getSimpleName(), throwable);
                onError.accept(throwable);
            }

            @Override
            public void onFailure(int statusCode) {
                Log.e("pttt", "Server-side error while loading " + getClass().getSimpleName());
                onError.accept(new StatusCodeException(statusCode));
            }
        });
    }

    public void add(@NonNull final TCreateDto createDto, @Nullable final Consumer<TDto> onAdded, final Consumer<Throwable> onError) {
        addImpl(createDto, parentId, new Database.RequestCallback<TDto>() {
            @Override
            public void onSuccess(TDto data, int statusCode) {
                if (callbacks != null)
                    callbacks.onCreated(data);
                if (onAdded != null)
                    onAdded.accept(data);

                String id = getId(data);
                ids.add(id);
                idsMap.put(id, data);
            }

            @Override
            public void onSuccess(int statusCode) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("pttt", "Error while loading " + getClass().getSimpleName(), throwable);
                onError.accept(throwable);
            }

            @Override
            public void onFailure(int statusCode) {
                Log.e("pttt", "Server-side error while loading " + getClass().getSimpleName());
                onError.accept(new StatusCodeException(statusCode));
            }
        });
    }

    public void get(String itemId, Consumer<TDto> onSuccess, Consumer<Throwable> onError){
        getImpl(itemId, new Database.RequestCallback<TDto>() {
            @Override
            public void onSuccess(TDto data, int statusCode) {
                if (onSuccess != null)
                    onSuccess.accept(data);
            }

            @Override
            public void onSuccess(int statusCode) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("pttt", "Error while loading " + getClass().getSimpleName(), throwable);
                onError.accept(throwable);
            }

            @Override
            public void onFailure(int statusCode) {
                Log.e("pttt", "Server-side error while loading " + getClass().getSimpleName());
                onError.accept(new StatusCodeException(statusCode));
            }
        });
    }

    public void update(final TUpdateDto updateDto, final String itemId, final Runnable onUpdated, final Consumer<Throwable> onError) {
        updateImpl(updateDto, itemId, new Database.EmptyRequestCallback() {
            @Override
            public void onSuccess(int statusCode) {
                if (onUpdated != null)
                    onUpdated.run();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("pttt", "Error while loading " + getClass().getSimpleName(), throwable);
                onError.accept(throwable);
            }

            @Override
            public void onFailure(int statusCode) {
                Log.e("pttt", "Server-side error while loading " + getClass().getSimpleName());
                onError.accept(new StatusCodeException(statusCode));
            }
        });
    }

    public void delete(@NonNull final TDto item, @Nullable Runnable onDeleted, final Consumer<Throwable> onError) {
        deleteImpl(getId(item), parentId, new Database.EmptyRequestCallback() {
            @Override
            public void onSuccess(int statusCode) {
                if (callbacks != null)
                    callbacks.onDeleted(item);
                if (onDeleted != null)
                    onDeleted.run();

                String id = getId(item);
                ids.remove(id);
                idsMap.remove(id);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("pttt", "Error while loading " + getClass().getSimpleName(), throwable);
                onError.accept(throwable);
            }

            @Override
            public void onFailure(int statusCode) {
                Log.e("pttt", "Server-side error while loading " + getClass().getSimpleName());
                onError.accept(new StatusCodeException(statusCode));
            }
        });
    }

    protected String[] ids() {
        return ids.toArray(new String[0]);
    }

    protected abstract void loadImpl(Database.RequestCallback<Map<String, TDto>> requestCallback);

    protected abstract void addImpl(TCreateDto createDto, final String parentId, Database.RequestCallback<TDto> requestCallback);

    protected abstract void getImpl(String itemId, Database.RequestCallback<TDto> requestCallback);

    protected abstract void updateImpl(final TUpdateDto updateDto, final String itemId, Database.EmptyRequestCallback requestCallback);

    protected abstract void deleteImpl(@NonNull final String itemId, final String parentId, Database.EmptyRequestCallback requestCallback);

    protected abstract String getId(@NonNull TDto obj);

    public void setCallbacks(RepositoryCallbacks<TDto> callbacks) {
        this.callbacks = callbacks;
    }

    public TDto getItem(int position){
        String id = ids.get(position);
        return idsMap.get(id);
    }

    public interface RepositoryCallbacks<T> {
        void onCreated(T item);

        void onDeleted(T item);
    }
}
