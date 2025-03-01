package com.sagiziv.connectx.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.sagiziv.connectx.R;

public class SelectableButton extends RelativeLayout {
    private TextView buttonTextView;
    private AppCompatImageView icon;
    private View.OnClickListener onClickListener;

    public SelectableButton(Context context) {
        super(context);
        init(context, null);
    }

    public SelectableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SelectableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setText(CharSequence text) {
        buttonTextView.setText(text);
    }

    public void setText(@StringRes int text) {
        buttonTextView.setText(text);
    }

    public void setTextSize(@Dimension float size) {
        buttonTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void getSetTextColor(@ColorInt int color) {
        buttonTextView.setTextColor(color);
    }

    public void disable(){
        View root = findViewById(R.id.icon_button_LAY_root);
        root.setOnClickListener(null);
    }

    public void enable(){
        View root = findViewById(R.id.icon_button_LAY_root);
        root.setOnClickListener(this::onClick);
    }

    @Override
    public void setOnClickListener(View.OnClickListener clickListener) {
        super.setOnClickListener(clickListener);
        this.onClickListener = clickListener;
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.icon_button_view_layout, this);
        findViews();

        ButtonProperties properties = attrs == null
                ? ButtonProperties.defaultValues(getContext())
                : ButtonProperties.fromAttributeSet(attrs, getContext());

        initializeRootView();
        initializeText(properties);
        initializeIcon(properties);
    }

    private void initializeRootView() {
        View root = findViewById(R.id.icon_button_LAY_root);
        root.setOnClickListener(this::onClick);
        root.setClickable(true);
    }

    private void initializeText(@NonNull ButtonProperties properties) {
        setTextSize(properties.textSize);
        getSetTextColor(properties.textColor);
        setText(properties.title);
    }

    private void initializeIcon(@NonNull ButtonProperties properties) {
        if (properties.iconResource != View.NO_ID)
            icon.setImageResource(properties.iconResource);
        else
            icon.setVisibility(View.GONE);
    }

    private void onClick(View v) {
        setSelected(true);
        if (getParent() instanceof SelectableButtonsGroup)
            ((SelectableButtonsGroup) getParent()).onButtonSelected(this);
        if (this.onClickListener != null)
            this.onClickListener.onClick(v);
    }

    private void findViews() {
        buttonTextView = findViewById(R.id.icon_button_LBL_text);
        icon = findViewById(R.id.icon_button_IMG_icon);
    }

    private static class ButtonProperties {
        public final String title;
        public final float textSize;
        @DrawableRes
        public final int iconResource;
        public final int textColor;

        private ButtonProperties(String title, float textSize, int iconResource, int textColor) {
            this.title = title;
            this.textSize = textSize;
            this.iconResource = iconResource;
            this.textColor = textColor;
        }

        @NonNull
        public static ButtonProperties defaultValues(Context context) {
            return new ButtonProperties(
                    "Button",
                    context.getResources().getDimension(R.dimen.label_text_size),
                    View.NO_ID,
                    ContextCompat.getColor(context, R.color.deep_azure));
        }

        public static ButtonProperties fromAttributeSet(@NonNull AttributeSet attributeSet, @NonNull Context context) {
            ButtonProperties base = defaultValues(context);
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SelectableButton);
            String title = typedArray.getString(R.styleable.SelectableButton_title);
            int iconResource = typedArray.getResourceId(R.styleable.SelectableButton_icon, base.iconResource);
            int textColor = typedArray.getColor(R.styleable.SelectableButton_textColor, base.textColor);
            float textSize = typedArray.getDimension(R.styleable.SelectableButton_textSize, base.textSize);

            typedArray.recycle();
            return new ButtonProperties(
                    title,
                    textSize,
                    iconResource,
                    textColor);
        }
    }
}
