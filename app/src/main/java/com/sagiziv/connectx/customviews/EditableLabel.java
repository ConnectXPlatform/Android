package com.sagiziv.connectx.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.sagiziv.connectx.R;

public class EditableLabel extends Label {
    public EditableLabel(@NonNull Context context) {
        super(context);
    }

    public EditableLabel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditableLabel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setColor(@ColorInt int color) {
        labelTextView.setTextColor(color);
    }

    public String getText(){
        return labelTextView.getText().toString();
    }

    @Override
    public void setPrefixWidthPercentage(float percentage) {
        if (percentage < 0) percentage = 0f;
        else if (percentage > 1) percentage = 1;
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams) prefixTextView.getLayoutParams();
        layoutParams.weight = percentage * 100;
        prefixTextView.setLayoutParams(layoutParams);

        View v = findViewById(R.id.textField);
        layoutParams = (LinearLayoutCompat.LayoutParams) v.getLayoutParams();
        layoutParams.weight = (1 - percentage) * 100;
        v.setLayoutParams(layoutParams);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.editable_label;
    }
}
