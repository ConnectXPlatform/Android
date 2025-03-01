package com.sagiziv.connectx;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.dto.Position;
import com.sagiziv.connectx.dto.Size;

public class Grid {

    private final int numRows, numCols;
    private final Rect bounds;
//    private final int maxWidth, maxHeight;

    public Grid(int numRows, int numCols, Rect bounds) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.bounds = bounds;
    }

    @NonNull
    public Size calculateViewSize(@NonNull Size gridSize) {
        int width = Math.round(calculateCellWidth() * gridSize.getWidth());
        int height = Math.round(calculateCellHeight() * gridSize.getHeight());
        return new Size(height, width);
    }

    @NonNull
    public Position calculateViewPosition(@NonNull Position gridPosition) {
        float x = calculateCellWidth() * gridPosition.getX() + bounds.left;
        float y = calculateCellHeight() * gridPosition.getY() + bounds.top;
        return new Position(x, y);
    }

    @NonNull
    public Position calculateGridPosition(@NonNull Position viewPosition) {
        float x = Math.round((viewPosition.getX() - bounds.left) / calculateCellWidth());
        float y = Math.round((viewPosition.getY() - bounds.top) / calculateCellHeight());
        return new Position(x, y);
    }

    public float snapXToGrid(float x) {
        if (x < 0)
            x = 0;
        if (x > bounds.width())
            x = bounds.width();

        int pos = Math.round(x / calculateCellWidth());
        return pos * calculateCellWidth() + bounds.left;
    }

    public float snapYToGrid(float y) {
        if (y < 0)
            y = 0;
        if (y > bounds.height())
            y = bounds.height();

        int pos = Math.round(y / calculateCellHeight());
        return pos * calculateCellHeight() + bounds.top;
    }

    private float calculateCellWidth() {
        return (float) bounds.width() / numCols;
    }

    private float calculateCellHeight() {
        return (float) bounds.height() / numRows;
    }
}
