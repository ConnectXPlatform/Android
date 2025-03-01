package com.sagiziv.connectx.fragments.components;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.mqtt.MqttHandler;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Collections;
import java.util.Objects;

public class PowerButtonComponentFragment extends DraggableComponentFragment<Integer> {
    private static final Integer PRESSED_STATE = 0;
    private static final Integer RELEASED_STATE = 1;
    private static final Integer POWER_OFF = 0;
    private static final Integer POWER_ON = 1;
    private final Object lock = new Object();
    private boolean sendState;
    private final Handler sendHandler = new Handler();
    private final Runnable sendTask = () -> {
        synchronized (lock) {
            if (sendState)
                sendMqttCall(PRESSED_STATE);
        }
    };

    private ImageView icon;
    private TextView header;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_power_button_component;
    }

    @Override
    protected void showComponent(PositionedComponent component) {
        header.setText(component.getLabel());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void findViews(View root) {
        header = root.findViewById(R.id.power_button_component_LBL_header);
        icon = root.findViewById(R.id.power_button_component_IMG);
        icon.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // View is pressed
                    // Handle press event here
//                    sendMqttCall(PRESSED_STATE);
                    sendState = true;
                    sendHandler.postDelayed(sendTask, 100);
                    break;
                case MotionEvent.ACTION_UP:
                    // View is released
                    // Handle release event here
                    synchronized (lock) {
                        sendState = false;
                        if (sendHandler.hasCallbacks(sendTask)) {
                            sendHandler.removeCallbacks(sendTask);
                            return true;
                        }
                    }
                    sendMqttCall(RELEASED_STATE);
                    break;
            }
            return true; // Return true to consume the touch event
        });
    }

    @Override
    protected void updateComponent(Integer payload) {
        if (Objects.equals(payload, POWER_ON)) {
            icon.setColorFilter(Color.rgb(0, 255, 0));
        } else {
            icon.setColorFilter(Color.rgb(255, 0, 0));
        }
    }

    @Override
    protected Integer deserializeData(String data) {
        return new Gson().fromJson(data, new TypeToken<Integer>() {
        });
    }

    private void sendMqttCall(int state) {
        try {
            MqttHandler.getInstance()
                    .sendMessageTo(component.getDeviceId(), Collections.singletonMap("newState", state), topic.getTopic());
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
