package com.sagiziv.connectx.repositories;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.Database;
import com.sagiziv.connectx.dto.ControlPanel;
import com.sagiziv.connectx.dto.CreateControlPanelDto;
import com.sagiziv.connectx.dto.UpdateControlPanelDto;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ControlPanelsRepository extends DataRepository<ControlPanel, CreateControlPanelDto, UpdateControlPanelDto> {
    private static ControlPanelsRepository instance;

    private ControlPanelsRepository(List<String> ids, String parentId) {
        super(ids, parentId);
    }

    public static void initialize(String[] ids, String parentId) {
        instance = new ControlPanelsRepository(Arrays.stream(ids).collect(Collectors.toList()), parentId);
    }

    public static ControlPanelsRepository getInstance() {
        return instance;
    }

    @Override
    protected void loadImpl(Database.RequestCallback<Map<String, ControlPanel>> requestCallback) {
        Database.getInstance()
                .getControlPanels(ids(), requestCallback);
    }

    @Override
    protected void addImpl(CreateControlPanelDto createDto, final String userId, Database.RequestCallback<ControlPanel> requestCallback) {
        Database.getInstance()
                .createControlPanel(createDto, userId, requestCallback);
    }

    @Override
    protected void getImpl(String itemId, Database.RequestCallback<ControlPanel> requestCallback) {
        Database.getInstance()
                .getControlPanel(itemId, requestCallback);
    }

    @Override
    protected void updateImpl(UpdateControlPanelDto updateControlPanelDto, String itemId, Database.EmptyRequestCallback requestCallback) {
        Database.getInstance()
                .updateControlPanel(updateControlPanelDto, itemId, requestCallback);
    }

    @Override
    protected void deleteImpl(@NonNull final String controlPanelId, final String parentId, Database.EmptyRequestCallback requestCallback) {
        Database.getInstance()
                .deleteControlPanel(controlPanelId, parentId, requestCallback);

    }

    @Override
    protected String getId(@NonNull ControlPanel obj) {
        return obj.getId();
    }
}
