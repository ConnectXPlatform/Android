package com.sagiziv.connectx.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.sagiziv.connectx.R;

public class SelectableButtonsGroup extends LinearLayoutCompat {
    private int currentlySelectedButton;
    private boolean isInflated;
    private SelectedButtonChangedCallback buttonChangedCallback;

    public SelectableButtonsGroup(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public SelectableButtonsGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SelectableButtonsGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (!isInflated) return;
        if (child instanceof SelectableButton && index != currentlySelectedButton) {
            SelectableButton button = (SelectableButton) child;
            button.setSelected(false);
        }
    }

    @Override
    public void removeView(View view) {
        final int index = indexOfChild(view);
        super.removeView(view);
        if (index >= 0) {
            onViewDeleted(view, index);
        }
    }

    @Override
    public void removeViewAt(int index) {
        View view = getChildAt(index);
        super.removeViewAt(index);
        onViewDeleted(view, index);
    }

    public int getSelectedButtonIndex() {
        return currentlySelectedButton;
    }

    public void onButtonSelected(SelectableButton button) {
        int index = indexOfChild(button);
        if (index == currentlySelectedButton) return;

        getChildAt(currentlySelectedButton).setSelected(false);
        currentlySelectedButton = index;
        if (buttonChangedCallback != null)
            buttonChangedCallback.onSelectedButtonChanged(index);
    }

    public void disableAllButtons(){
        for (int i = 0; i < getChildCount(); i++) {
            ((SelectableButton) getChildAt(i)).disable();
        }
    }

    public void enableAllButtons(){
        for (int i = 0; i < getChildCount(); i++) {
            ((SelectableButton) getChildAt(i)).enable();
        }
    }

    public void setButtonChangedCallback(SelectedButtonChangedCallback buttonChangedCallback) {
        this.buttonChangedCallback = buttonChangedCallback;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        isInflated = true;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof SelectableButton)) continue;
            SelectableButton button = (SelectableButton) child;
            button.setSelected(i == currentlySelectedButton);
        }
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectableButtonsGroup);
            currentlySelectedButton = typedArray.getInt(R.styleable.SelectableButtonsGroup_defaultSelectedView, -1);
            typedArray.recycle();
        }
    }

    private void onViewDeleted(@NonNull View view, final int index) {

        if (view instanceof SelectableButton) {
            // If we deleted the open view, none of the views is open anymore.
            if (index == currentlySelectedButton) currentlySelectedButton = -1;
            // If the deleted view is before the open view, it mean the open view moves up in the hierarchy.
            else if (index < currentlySelectedButton) currentlySelectedButton--;
        }
    }

    public interface SelectedButtonChangedCallback{
        void onSelectedButtonChanged(int newIndex);
    }
}
