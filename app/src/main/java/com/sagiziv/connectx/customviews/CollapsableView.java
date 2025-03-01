package com.sagiziv.connectx.customviews;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.sagiziv.connectx.R;

public class CollapsableView extends RelativeLayout {
    public static final int CLOSE = 10;
    public static final int OPEN = 20;

    private StateChangedCallback stateChangedCallback;
    private LinearLayoutCompat collapsableViewContent;
    private AppCompatImageView stateIcon;
    private TextView titleTextView;
    @State
    private int currentState;

    //<editor-fold desc="Constructors">
    public CollapsableView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public CollapsableView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CollapsableView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public CollapsableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    //</editor-fold>

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (collapsableViewContent != null) {
            collapsableViewContent.addView(child, index, params);
        } else {
            super.addView(child, index, params);
        }
    }

    public void setStateChangedCallback(@Nullable StateChangedCallback stateChangedCallback) {
        this.stateChangedCallback = stateChangedCallback;
    }

    public void setState(@State int state) {
        if (state == currentState) return;
        currentState = state;
        collapsableViewContent.setVisibility(state == CLOSE ? View.GONE : View.VISIBLE);
        stateIcon.setImageResource(currentState == OPEN ? R.drawable.ic_minus : R.drawable.ic_plus);
        if (stateChangedCallback != null) {
            stateChangedCallback.onStateChanged(currentState);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Enable the animation in the parent view.
        LayoutTransition transition = new LayoutTransition();
        ((ViewGroup) super.getParent()).setLayoutTransition(transition);
        transition.enableTransitionType(LayoutTransition.CHANGING);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.collapsable_view_layout, this);
        findViews();

        CollapsableProperties properties = attrs == null
                ? CollapsableProperties.defaultValues(getContext())
                : CollapsableProperties.fromAttributeSet(attrs, getContext());

        this.<ViewGroup>findViewById(R.id.collapsable_view_root).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);
        collapsableViewContent.setBackgroundColor(properties.contentBackgroundColor);
        titleTextView.setOnClickListener(v -> setState(currentState == OPEN ? CLOSE : OPEN));
        initializeTitle(properties);
        setState(properties.state);
    }

    private void findViews() {
        collapsableViewContent = findViewById(R.id.collapsable_view_content);
        stateIcon = findViewById(R.id.collapsable_IMG_state_icon);
        titleTextView = findViewById(R.id.collapsable_view_title);
    }

    private void initializeTitle(@NonNull CollapsableProperties properties) {
        titleTextView.setText(properties.title);
        titleTextView.setTextColor(properties.titleColor);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, properties.titleSize);
        ImageViewCompat.setImageTintList(stateIcon, ColorStateList.valueOf(properties.titleColor));
    }

    @IntDef(value = {OPEN, CLOSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public interface StateChangedCallback {
        void onStateChanged(@State int newState);
    }

    private static class CollapsableProperties {
        public final String title;
        public final float titleSize;
        public final int titleColor;
        public final int contentBackgroundColor;
        public final int state;

        private CollapsableProperties(String title, float titleSize, int titleColor, int contentBackgroundColor, int state) {
            this.title = title;
            this.titleSize = titleSize;
            this.titleColor = titleColor;
            this.contentBackgroundColor = contentBackgroundColor;
            this.state = state;
        }

        @NonNull
        public static CollapsableProperties defaultValues(Context context) {
            return new CollapsableProperties(
                    "",
                    context.getResources().getDimension(R.dimen.sub_title_size),
                    ContextCompat.getColor(context, R.color.white),
                    ContextCompat.getColor(context, R.color.deep_azure),
                    OPEN
            );
        }

        public static CollapsableProperties fromAttributeSet(@NonNull AttributeSet attributeSet, @NonNull Context context) {
            CollapsableProperties base = defaultValues(context);
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CollapsableView);
            String title = typedArray.getString(R.styleable.CollapsableView_title);
            int contentBackgroundColor = typedArray.getColor(R.styleable.CollapsableView_contentBackground, base.contentBackgroundColor);
            int initialState = typedArray.getInt(R.styleable.CollapsableView_state, base.state);
            int textColor = typedArray.getColor(R.styleable.CollapsableView_titleColor, base.titleColor);
            float textSize = typedArray.getDimension(R.styleable.CollapsableView_titleSize, base.titleSize);

            typedArray.recycle();
            return new CollapsableProperties(
                    title,
                    textSize,
                    textColor,
                    contentBackgroundColor,
                    initialState
            );
        }
    }
}
