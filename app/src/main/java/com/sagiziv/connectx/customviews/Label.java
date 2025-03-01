package com.sagiziv.connectx.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.sagiziv.connectx.R;

public class Label extends LinearLayoutCompat {
    protected TextView prefixTextView, labelTextView;

    public Label(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public Label(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Label(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setText(CharSequence text) {
        labelTextView.setText(text);
    }

    public void setText(@StringRes int textResource) {
        labelTextView.setText(textResource);
    }

    public void setPrefixText(CharSequence text) {
        prefixTextView.setText(text);
    }

    public void setPrefixText(@StringRes int textResource) {
        prefixTextView.setText(textResource);
    }

    public void setColor(@ColorInt int color) {
        prefixTextView.setTextColor(color);
        labelTextView.setTextColor(color);
    }

    public void setPrefixTextSize(@Dimension float size) {
        prefixTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setLabelTextSize(@Dimension float size) {
        labelTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setPrefixWidthPercentage(float percentage) {
        if (percentage < 0) percentage = 0f;
        else if (percentage > 1) percentage = 1;
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams) prefixTextView.getLayoutParams();
        layoutParams.weight = percentage * 100;
        prefixTextView.setLayoutParams(layoutParams);

        layoutParams = (LinearLayoutCompat.LayoutParams) labelTextView.getLayoutParams();
        layoutParams.weight = (1 - percentage) * 100;
        labelTextView.setLayoutParams(layoutParams);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        inflate(context, getLayoutResource(), this);
        findViews();
        LabelProperties properties = attrs == null ? LabelProperties.defaultValues(getContext()) : LabelProperties.fromAttributeSet(attrs, getContext());

        properties.validate();

        prefixTextView.setLabelFor(labelTextView.getId());
        setPrefixText(properties.prefix);
        setText(properties.defaultLabel);
        setPrefixTextSize(properties.prefixTextSize);
        setLabelTextSize(properties.labelTextSize);
        setColor(properties.textColor);
        setPrefixWidthPercentage(properties.prefixWidthPercentage);
    }

    private void findViews() {
        prefixTextView = findViewById(R.id.label_prefix);
        labelTextView = findViewById(R.id.label_text);
    }

    @LayoutRes
    protected int getLayoutResource() {
        return R.layout.label;
    }

    private static class LabelProperties {
        public final String prefix;
        public final String defaultLabel;
        public final float prefixTextSize;
        public final float labelTextSize;
        public final int textColor;
        private final float prefixWidthPercentage;

        public LabelProperties(String prefix, String defaultLabel, float prefixTextSize, float labelTextSize, int textColor, float prefixWidthPercentage) {
            this.prefix = prefix;
            this.defaultLabel = defaultLabel;
            this.prefixTextSize = prefixTextSize;
            this.labelTextSize = labelTextSize;
            this.textColor = textColor;
            this.prefixWidthPercentage = prefixWidthPercentage;
        }

        public void validate() {
            if (prefixWidthPercentage > 1 || prefixWidthPercentage < 0)
                throw new RuntimeException("Invalid prefix width percentage: " + prefixWidthPercentage + ". Value must be in range [0, 1]");
        }

        @NonNull
        public static LabelProperties defaultValues(Context context) {
            float labelTextSize = context.getResources().getDimension(R.dimen.label_text_size);
            float prefixTextSize = context.getResources().getDimension(R.dimen.label_prefix_size);
            return new LabelProperties("", "", prefixTextSize, labelTextSize, ContextCompat.getColor(context, R.color.white), 0.25f);
        }

        @NonNull
        public static LabelProperties fromAttributeSet(@NonNull AttributeSet attributeSet, @NonNull Context context) {
            LabelProperties base = defaultValues(context);
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.Label);
            String prefix = typedArray.getString(R.styleable.Label_prefix);
            String labelText = typedArray.getString(R.styleable.Label_defaultText);
            float prefixTextSize = typedArray.getDimension(R.styleable.Label_prefixTextSize, base.prefixTextSize);
            float labelTextSize = typedArray.getDimension(R.styleable.Label_labelTextSize, base.labelTextSize);
            int textColor = typedArray.getColor(R.styleable.Label_textColor, base.textColor);
            float prefixWidthPercentage = typedArray.getFloat(R.styleable.Label_prefixWidthPercentage, base.prefixWidthPercentage);

            typedArray.recycle();
            return new LabelProperties(prefix, labelText, prefixTextSize, labelTextSize, textColor, prefixWidthPercentage);
        }
    }
}
