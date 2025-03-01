package com.sagiziv.connectx.customviews;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.sagiziv.connectx.R;

public class CollapsableViewGroup extends LinearLayoutCompat {
    private int currentlyOpenedView;
    private boolean isInflated;

    //<editor-fold desc="Constructors">
    public CollapsableViewGroup(Context context) {
        super(context);
        isInflated = false;
        init(context, null);
    }

    public CollapsableViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        isInflated = false;
        init(context, attrs);
    }

    public CollapsableViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isInflated = false;
        init(context, attrs);
    }
    //</editor-fold>

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Enable change animations on the parent view.
        LayoutTransition transition = new LayoutTransition();
        ((ViewGroup) super.getParent()).setLayoutTransition(transition);
        transition.enableTransitionType(LayoutTransition.CHANGING);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (isInEditMode() || !isInflated) return;
        if (child instanceof CollapsableView && index != currentlyOpenedView) {
            CollapsableView collapsableView = (CollapsableView) child;
            registerCallback(collapsableView, index);
            collapsableView.setState(CollapsableView.CLOSE);
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        isInflated = true;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof CollapsableView)) continue;
            CollapsableView collapsableView = (CollapsableView) child;
            if (!isInEditMode() && i != currentlyOpenedView)
                collapsableView.setState(CollapsableView.CLOSE);
            registerCallback(collapsableView, i);
        }
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        setOrientation(VERTICAL);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CollapsableViewGroup);
            currentlyOpenedView = typedArray.getInt(R.styleable.CollapsableViewGroup_defaultOpenedView, -1);
            typedArray.recycle();
        }
    }

    private void onViewDeleted(@NonNull View view, final int index) {

        if (view instanceof CollapsableView) {
            // If we deleted the open view, none of the views is open anymore.
            if (index == currentlyOpenedView) currentlyOpenedView = -1;
                // If the deleted view is before the open view, it mean the open view moves up in the hierarchy.
            else if (index < currentlyOpenedView) currentlyOpenedView--;
        }
    }

    private void onChildOpened(int childIndex) {
        currentlyOpenedView = childIndex;
        for (int i = 0; i < getChildCount(); i++) {
            // The child already changed, we can skip it.
            if (i == childIndex) continue;
            View child = getChildAt(i);
            if (!(child instanceof CollapsableView)) continue;
            CollapsableView collapsableView = (CollapsableView) child;
            collapsableView.setStateChangedCallback(null);
            collapsableView.setState(CollapsableView.CLOSE);
            registerCallback(collapsableView, i);
        }
    }

    private void registerCallback(@NonNull CollapsableView collapsableView, final int childIndex) {
        collapsableView.setStateChangedCallback(newState -> {
            if (newState == CollapsableView.OPEN)
                onChildOpened(childIndex);
            else
                currentlyOpenedView = -1;
        });
    }
}
