package com.sagiziv.connectx.repositories;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.Database;
import com.sagiziv.connectx.dto.ControlPanel;
import com.sagiziv.connectx.dto.CreatePositionedComponent;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.dto.UpdatePositionedComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class PositionedComponentsRepository extends DataRepository<PositionedComponent, CreatePositionedComponent, UpdatePositionedComponent> {
    public PositionedComponentsRepository(@NonNull ControlPanel controlPanel) {
        super(new ArrayList<>(Arrays.asList(controlPanel.getComponents())), controlPanel.getId());
    }

    @Override
    protected void loadImpl(Database.RequestCallback<Map<String, PositionedComponent>> requestCallback) {
        Database.getInstance()
                .getPositionedComponents(ids(), requestCallback);
    }

    @Override
    protected void addImpl(CreatePositionedComponent createPositionedComponent, String controlPanelId, Database.RequestCallback<PositionedComponent> requestCallback) {
        Database.getInstance()
                .createPositionedComponent(createPositionedComponent, controlPanelId, requestCallback);
    }

    @Override
    protected void getImpl(String itemId, Database.RequestCallback<PositionedComponent> requestCallback) {
        Database.getInstance()
                .getPositionedComponent(itemId, requestCallback);
    }

    @Override
    protected void updateImpl(UpdatePositionedComponent updatePositionedComponent, String componentId, Database.EmptyRequestCallback requestCallback) {
        Database.getInstance()
                .updatePositionedComponent(updatePositionedComponent, componentId, requestCallback);
    }

    @Override
    protected void deleteImpl(@NonNull String componentId, String controlPanelId, Database.EmptyRequestCallback requestCallback) {
        Database.getInstance()
                .deletePositionedComponent(componentId, controlPanelId, requestCallback);
    }

    @Override
    protected String getId(@NonNull PositionedComponent obj) {
        return obj.getId();
    }
}
