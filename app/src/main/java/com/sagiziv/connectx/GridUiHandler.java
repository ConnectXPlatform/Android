package com.sagiziv.connectx;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.fragments.components.DraggableComponentFragment;

public class GridUiHandler {

    private final Grid grid;

    public GridUiHandler(Grid grid) {
        this.grid = grid;
    }

    public void PlaceComponents(@NonNull DraggableComponentFragment[] components) {
//        for (DraggableComponentFragment draggableComponentFragment : components) {
//            PositionedComponent component = draggableComponentFragment.getComponent();
//            Size screenSize = grid.calculateViewSize(component.getSize());
//            Position screenPosition = grid.calculateViewPosition(component.getPosition());
//            draggableComponentFragment.setPosition(screenPosition);
//            draggableComponentFragment.setSize(screenSize);
//        }
    }
}
