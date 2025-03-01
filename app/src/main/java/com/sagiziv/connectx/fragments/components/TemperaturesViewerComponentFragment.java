package com.sagiziv.connectx.fragments.components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagiziv.connectx.FeedbackHandler;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.customviews.Label;
import com.sagiziv.connectx.dataadapters.BaseAdapter;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.mqtt.MqttHandler;
import com.sagiziv.connectx.mqtt.ResponseMessage;
import com.sagiziv.connectx.utils.AndroidUtils;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class TemperaturesViewerComponentFragment extends DraggableComponentFragment<ArrayList<TemperaturesViewerComponentFragment.TemperatureReading>> {
    private TextView header;
    private ImageView refreshButton;
    private ObjectAnimator rotationAnimator;
    private TemperaturesAdapterView tempsViewAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_temps_view_component;
    }

    @Override
    protected void showComponent(PositionedComponent component) {
        header.setText(component.getLabel());
    }

    @Override
    protected void findViews(View root) {
        header = root.findViewById(R.id.temps_view_component_LBL_header);
        refreshButton = root.findViewById(R.id.temps_view_component_BTN_refresh);
        RecyclerView tempsView = root.findViewById(R.id.temps_view_component_LST_data);
        tempsViewAdapter = new TemperaturesAdapterView(root.getContext());
        tempsView.setAdapter(tempsViewAdapter);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            private final ArrayList<TemperatureReading> response = new ArrayList<>();

            @Override
            public void onClick(View v) {
                if (rotationAnimator != null && rotationAnimator.isRunning())
                    return;
                startRotation();
                response.clear();
                try {
                    MqttHandler.getInstance()
                            .sendMessageTo(component.getDeviceId(), Collections.singletonMap("chunkSize", 2),
                                    topic.getTopic(), responseCallback);
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }

            private final MqttHandler.MessageResponseCallback responseCallback = new MqttHandler.MessageResponseCallback() {
                @Override
                protected void processResponseImpl(MqttHandler.IncomingMessage message, byte status) {
                    TemperatureReading[] readings = message.getPayload((GenericArrayType) () -> TemperatureReading.class);
                    response.addAll(Arrays.asList(readings));
                    if (status == ResponseMessage.ResponseStatus.DONE) {
                        AndroidUtils.runOnMainThread(() -> {
                            rotationAnimator.end();
                            updateComponent(response);
                        });
                    }
                }

                @Override
                protected void onErrorImpl(String error) {
                    AndroidUtils.runOnMainThread(() -> {
                        rotationAnimator.end();
                        FeedbackHandler.getInstance().toast("An error occurred:\n" + error);
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
    protected void updateComponent(ArrayList<TemperatureReading> payload) {
        tempsViewAdapter.clear();
        tempsViewAdapter.addItems(payload);
    }

    public final static class TemperatureReading {
        private String address;
        private float value;
        private int unit;

        public String getAddress() {
            return address;
        }

        public TemperatureReading setAddress(String address) {
            this.address = address;
            return this;
        }

        public float getValue() {
            return value;
        }

        public TemperatureReading setValue(float value) {
            this.value = value;
            return this;
        }

        public int getUnit() {
            return unit;
        }

        public TemperatureReading setUnit(int unit) {
            this.unit = unit;
            return this;
        }
    }

    public final static class TemperaturesAdapterView extends BaseAdapter<TemperatureReading> {

        public TemperaturesAdapterView(Context context) {
            super(R.layout.temperature_reading_entry, context);
        }

        @Override
        protected BaseAdapter<TemperatureReading>.ViewHolder createViewHolder(View view) {
            return new TempsViewHolder(view);
        }

        public class TempsViewHolder extends ViewHolder {
            private Label label;

            public TempsViewHolder(@NonNull View itemView) {
                super(itemView);
            }

            @Override
            protected void showData(TemperatureReading data) {
                label.setPrefixText(data.getAddress());
                label.setText(String.format(Locale.getDefault(), "%.2f° %c", data.getValue(),
                        data.getUnit() == 0 ? 'C' : 'F'));
            }

            @Override
            protected void findViews(View itemView) {
                label = (Label) itemView;
            }
        }
    }
}
