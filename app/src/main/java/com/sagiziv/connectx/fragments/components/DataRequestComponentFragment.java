package com.sagiziv.connectx.fragments.components;

import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sagiziv.connectx.FeedbackHandler;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.mqtt.MqttHandler;
import com.sagiziv.connectx.utils.AndroidUtils;
import com.sagiziv.connectx.utils.DialogUtils;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Collections;

public class DataRequestComponentFragment extends DraggableComponentFragment<Object> {
    private TextView header;
    private ImageView refreshButton;
    private ObjectAnimator rotationAnimator;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_data_request_component;
    }

    @Override
    protected void showComponent(PositionedComponent component) {
        header.setText(component.getLabel());
    }

    @Override
    protected void findViews(View root) {
        header = root.findViewById(R.id.data_request_component_LBL_header);
        refreshButton = root.findViewById(R.id.data_request_component_BTN_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (rotationAnimator != null && rotationAnimator.isRunning())
                    return;
                startRotation();
                try {
                    MqttHandler.getInstance()
                            .sendMessageTo(component.getDeviceId(), Collections.emptyMap(), topic.getTopic(),
                                    responseCallback);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }

            private final MqttHandler.MessageResponseCallback responseCallback = new MqttHandler.MessageResponseCallback() {
                @Override
                protected void processResponseImpl(MqttHandler.IncomingMessage message, byte status) {
                    Object response = new Gson().fromJson(message.getPayload(), new TypeToken<Object>() {
                    });
                    AndroidUtils.runOnMainThread(() -> {
                        rotationAnimator.end();
                        updateComponent(response);
                    });
                }

                @Override
                protected void onErrorImpl(String error) {
                    AndroidUtils.runOnMainThread(() -> {
                        rotationAnimator.end();
                        FeedbackHandler.getInstance().toast("An error occurred: " + error);
                    });

                }

                @Override
                protected void onTimeoutImpl(String error) {
                    FeedbackHandler.getInstance().toast(error);
                    AndroidUtils.runOnMainThread(rotationAnimator::end);
                }
            };
        });
    }

    private void startRotation() {
        rotationAnimator = ObjectAnimator.ofFloat(refreshButton, View.ROTATION, 0f, 360f);
        rotationAnimator.setDuration(1000); // Set the duration of rotation
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Repeat indefinitely for continuous rotation
        rotationAnimator.start();
    }

    @Override
    protected void updateComponent(Object payload) {
        String text = new GsonBuilder().setPrettyPrinting().create().toJson(payload);
        DialogUtils.createDialog(header.getContext())
                .withTextContent(text)
                .withPositiveButton("Copy", dialog -> {
                    ClipboardManager clipboard = (ClipboardManager) header.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(topic.getTopic(), text);
                    clipboard.setPrimaryClip(clip);
                })
                .withTitle(topic.getTopic())
                .isCancelable(true)
                .getDialog()
                .show();
    }
}
