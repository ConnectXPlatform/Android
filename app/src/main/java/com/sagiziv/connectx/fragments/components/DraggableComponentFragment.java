package com.sagiziv.connectx.fragments.components;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.Position;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.dto.TopicInfo;
import com.sagiziv.connectx.mqtt.MqttHandler;
import com.sagiziv.connectx.mqtt.topics.BroadcastTopicBuilder;
import com.sagiziv.connectx.utils.AndroidUtils;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.jetbrains.annotations.NotNull;

public abstract class DraggableComponentFragment<T> extends Fragment {
    protected PositionedComponent component;
    protected TopicInfo topic;
    private View dragDetector;
    private Position newPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        findViews(view);
        dragDetector = view.findViewById(R.id.draggable_VIEW_drag);
        dragDetector.setOnLongClickListener(v -> {
            Log.d("pttt", "Drag handler");
            ClipData.Item clipText = new ClipData.Item("");
            String[] mimeTypes = new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData("", mimeTypes, clipText);

            View parent = (View) v.getParent();
            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(parent);
            (parent).startDragAndDrop(data, dragShadowBuilder, new LocalStateHolder(parent, this), 0);
            return true;
        });
        dragDetector.setOnClickListener(v -> showEditDialog());

        return view;
    }

    @LayoutRes
    protected abstract int getLayoutRes();

    protected abstract void showComponent(PositionedComponent component);

    protected abstract void findViews(View root);

    public void positionChanged(Position newPosition) {
        this.newPosition = newPosition;
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public PositionedComponent getComponent() {
        return component;
    }

    public void loadComponent(@NotNull PositionedComponent component, @NonNull TopicInfo topicInfo) {
        this.component = component;
        topic = topicInfo;
        showComponent(component);

        if (!topicInfo.isBroadcastSender()) return;

        // Subscribe to broadcasts on that topic from this device
        String topicPattern = new BroadcastTopicBuilder()
                .fromSender(component.getDeviceId())
                .addTopicSegment(topicInfo.getTopic())
                .withAnyPayloadFormat()
                .build();
        try {
            MqttHandler.getInstance()
                    .subscribeTo(topicPattern, incomingMessage -> {
//                        BroadcastTopic broadcast = BroadcastTopic.fromString(incomingMessage.getTopic());
                        T payload = deserializeData(incomingMessage.getPayload());
                        AndroidUtils.runOnMainThread(() -> {
                            updateComponent(payload);
                        });
                    });
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        String topicPattern = new BroadcastTopicBuilder()
                .fromSender(component.getDeviceId())
                .addTopicSegment(topic.getTopic())
                .withAnyPayloadFormat()
                .build();
        try {
            MqttHandler.getInstance()
                    .unsubscribeFrom(topicPattern);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void updateComponent(T payload);

    protected T deserializeData(String data) {
        return new Gson().fromJson(data, new TypeToken<T>() {
        });
    }

    private void showEditDialog() {

    }

    public void enterEditMode() {
        enableDrag();
    }

    public void exitEditMode() {
        disableDrag();
    }

    private void enableDrag() {
        dragDetector.setVisibility(View.VISIBLE);
    }

    private void disableDrag() {
        dragDetector.setVisibility(View.GONE);
    }

    public static final class LocalStateHolder {
        private final View parent;
        private final DraggableComponentFragment<?> componentFragment;

        public LocalStateHolder(View parent, DraggableComponentFragment<?> componentFragment) {
            this.parent = parent;
            this.componentFragment = componentFragment;
        }

        public View getParent() {
            return parent;
        }

        public DraggableComponentFragment<?> getComponentFragment() {
            return componentFragment;
        }
    }
}
