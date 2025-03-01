package com.sagiziv.connectx.fragments.components;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.mqtt.MqttHandler;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Collections;

public class ButtonComponentFragment extends DraggableComponentFragment<Object> {
    private TextView label;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_button_component;
    }

    @Override
    protected void showComponent(PositionedComponent component) {
        label.setText(component.getLabel());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void findViews(View root) {
        label = root.findViewById(R.id.button_component_LBL_label);
        label.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // View is pressed
                    // Handle press event here
                    sendMqttCall(1);
                    break;
                case MotionEvent.ACTION_UP:
                    // View is released
                    // Handle release event here
                    sendMqttCall(0);
                    break;
            }
            return true; // Return true to consume the touch event
        });
    }

    @Override
    protected void updateComponent(Object payload) {
        label.setText(payload.toString());
    }

    private void sendMqttCall(int status) {
        try {
            if (topic.isDataProvider())
                MqttHandler.getInstance()
                        .sendMessageTo(component.getDeviceId(), Collections.emptyMap(), topic.getTopic(),
                                responseCallback);
            else
                MqttHandler.getInstance()
                        .sendMessageTo(component.getDeviceId(), status, topic.getTopic());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    private void processResponse(MqttHandler.IncomingMessage message, byte status) {
    }

    private final MqttHandler.MessageResponseCallback responseCallback = new MqttHandler.MessageResponseCallback() {
        @Override
        protected void processResponseImpl(MqttHandler.IncomingMessage message, byte status) {
            Log.d("pttt", "response: " + message);
            label.setText(message.getPayload(Object.class).toString());
        }

        @Override
        protected void onErrorImpl(String error) {

        }

        @Override
        protected void onTimeoutImpl(String error) {

        }
    };
}
