package com.sagiziv.connectx.repositories;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.Database;
import com.sagiziv.connectx.dto.CreateDeviceDto;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.UpdateDeviceDto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class DevicesRepository extends DataRepository<DeviceInfo, CreateDeviceDto, UpdateDeviceDto> {
    private static DevicesRepository instance;

    private DevicesRepository(List<String> ids, String parentId) {
        super(ids, parentId);
    }

    public static void initialize(String[] ids, String parentId) {
        instance = new DevicesRepository(Arrays.stream(ids).collect(Collectors.toList()), parentId);
    }

    public static DevicesRepository getInstance() {
        return instance;
    }

    @Override
    protected void loadImpl(Database.RequestCallback<Map<String, DeviceInfo>> requestCallback) {
        Database.getInstance()
                .getDevices(ids(), requestCallback);
    }

    @Override
    protected void addImpl(CreateDeviceDto createDeviceDto, String userId, Database.RequestCallback<DeviceInfo> requestCallback) {
        Database.getInstance()
                .createDevice(createDeviceDto, userId, requestCallback);
    }

    @Override
    protected void getImpl(String itemId, Database.RequestCallback<DeviceInfo> requestCallback) {
        Database.getInstance()
                .getDevice(itemId, requestCallback);
    }

    @Override
    protected void updateImpl(UpdateDeviceDto updateDeviceDto, String itemId, Database.EmptyRequestCallback requestCallback) {
        Database.getInstance()
                .updateDevice(itemId, updateDeviceDto, requestCallback);
    }

    @Override
    protected void deleteImpl(@NonNull String deviceId, String userId, Database.EmptyRequestCallback requestCallback) {
        Database.getInstance()
                .deleteDevice(deviceId, userId, requestCallback);
    }

    @Override
    protected String getId(@NonNull DeviceInfo obj) {
        return obj.getId();
    }
}
