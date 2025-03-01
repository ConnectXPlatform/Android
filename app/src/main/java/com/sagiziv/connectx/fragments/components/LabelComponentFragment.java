package com.sagiziv.connectx.fragments.components;

import android.view.View;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.customviews.Label;
import com.sagiziv.connectx.dto.PositionedComponent;

public class LabelComponentFragment extends DraggableComponentFragment<Object> {
    private Label label;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_label_component;
    }

    @Override
    protected void showComponent(PositionedComponent component) {
        label.setPrefixText(component.getLabel());
    }

    @Override
    protected void findViews(View root) {
        label = root.findViewById(R.id.label_component_LBL_label);
    }

    @Override
    protected void updateComponent(Object payload) {
        label.setText(payload.toString());
    }
}
