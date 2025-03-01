package com.sagiziv.connectx.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.LayoutRes;

import com.google.android.material.slider.Slider;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.CreatePositionedComponent;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.Position;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.dto.Size;
import com.sagiziv.connectx.fragments.components.ComponentsFactory;
import com.sagiziv.connectx.repositories.DevicesRepository;

import java.util.ArrayList;
import java.util.Collection;

public class ComponentEditDialog {

    private EditText label;
    private AutoCompleteTextView component, device, topic;
    private Slider widthSlider, heightSlider;
    private Button dialogPositiveButton;

    private final PositionedComponent editedComponent;

    public ComponentEditDialog() {
        editedComponent = null;
    }

    public ComponentEditDialog(PositionedComponent editedComponent) {
        this.editedComponent = editedComponent;
    }

    @LayoutRes
    public int getLayoutResource() {
        return R.layout.dialog_create_component;
    }

    public CreatePositionedComponent create() {
        return new CreatePositionedComponent()
                .setComponentId(component.getText().toString())
                .setLabel(label.getText().toString())
                .setDeviceId(device.getText().toString())
                .setTopicId(topic.getText().toString())
                .setPosition(new Position(0, 0))
                .setSize(new Size((int) heightSlider.getValue(), (int) widthSlider.getValue()));
    }

    public void initializeViews(DialogInterface dialogInterface) {
        AlertDialog dialog = (AlertDialog) dialogInterface;
        dialogPositiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        label = dialog.findViewById(R.id.create_component_INP_label);
        component = dialog.findViewById(R.id.create_component_INP_component);
        device = dialog.findViewById(R.id.create_component_INP_device);
        topic = dialog.findViewById(R.id.create_component_INP_topic);
        widthSlider = dialog.findViewById(R.id.create_component_INP_width);
        heightSlider = dialog.findViewById(R.id.create_component_INP_height);

        widthSlider.setValueFrom(1);
        widthSlider.setValueTo(2);
        widthSlider.setValue(1);
        heightSlider.setValueFrom(1);
        heightSlider.setValueTo(2);
        heightSlider.setValue(1);
//        addValidation();
        if (editedComponent != null) {
            label.setText(editedComponent.getLabel());
            widthSlider.setValue(editedComponent.getSize().getWidth());
            heightSlider.setValue(editedComponent.getSize().getHeight());
        }
        initializeDropdowns(dialog);
    }

    private void initializeDropdowns(AlertDialog dialog) {
        ArrayAdapter<String> componentsAdapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line);
        componentsAdapter.addAll(ComponentsFactory.getComponentsIds());
        component.setAdapter(componentsAdapter);

        ArrayAdapter<String> devicesAdapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line);
        Collection<String> ids = DevicesRepository.getInstance().getIds();
        devicesAdapter.addAll(ids);
        device.setAdapter(devicesAdapter);

        device.setOnItemClickListener((parent, view, position, id) -> {
            ArrayAdapter<String> topicsAdapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_dropdown_item_1line);
            DeviceInfo item = DevicesRepository.getInstance().getItem(position);
            ArrayList<String> topics = item.getTopics();
            topicsAdapter.addAll(topics);
            topic.setAdapter(topicsAdapter);
        });
    }
}
