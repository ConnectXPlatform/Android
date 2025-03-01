package com.sagiziv.connectx;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagiziv.connectx.dto.ControlPanel;
import com.sagiziv.connectx.dto.CreateControlPanelDto;
import com.sagiziv.connectx.dto.CreateDeviceDto;
import com.sagiziv.connectx.dto.CreatePositionedComponent;
import com.sagiziv.connectx.dto.CreateTopicDto;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.NewUser;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.dto.TopicInfo;
import com.sagiziv.connectx.dto.UpdateControlPanelDto;
import com.sagiziv.connectx.dto.UpdateDeviceDto;
import com.sagiziv.connectx.dto.UpdatePositionedComponent;
import com.sagiziv.connectx.dto.UpdateTopicDto;
import com.sagiziv.connectx.dto.User;
import com.sagiziv.connectx.dto.UserCreated;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Database {
    //<editor-fold desc="Constants">
    private static final String DEVICES_ROUTE = "devices";
    private static final String CONTROL_PANELS_ROUTE = "control_panels";
    private static final String COMPONENTS_ROUTE = "components";
    private static final String POSITIONED_COMPONENTS_ROUTE = "positioned_components";
    private static final String TOPICS_ROUTE = "topics";
    //</editor-fold>

    //<editor-fold desc="Variables">
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final HttpUrl serverUrl;
    private static Database instance;
    //</editor-fold>

    //<editor-fold desc="Singleton implementation">
    private Database(final String serverUrl, final int port) {
        this.serverUrl = new HttpUrl.Builder()
                .port(port)
                .host(serverUrl)
                .scheme("http")
                .build();
        gson = new Gson();
        httpClient = new OkHttpClient();
    }

    public static Database getInstance() {
        return instance;
    }

    public static void initialize(final String serverUrl, final int port) {
        if (instance != null) return;
        instance = new Database(serverUrl, port);
    }
    //</editor-fold>

    //<editor-fold desc="DeviceInfo API">
    public void createDevice(final CreateDeviceDto createDeviceDto, final String userId, final RequestCallback<DeviceInfo> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment("users")
                .addPathSegment(userId)
                .addPathSegment(DEVICES_ROUTE);
        Log.d("pttt", "Requesting device from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .post(createRequestBody(createDeviceDto));
        sendRequest(request, requestCallback, new TypeToken<DeviceInfo>() {
        });
    }

    public void getDevice(final String deviceId, final RequestCallback<DeviceInfo> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(DEVICES_ROUTE)
                .addPathSegment(deviceId);
        Log.d("pttt", "Requesting device from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .get();
        sendRequest(request, requestCallback, new TypeToken<DeviceInfo>() {
        });
    }

    public void getDevices(String[] devicesIds, RequestCallback<Map<String, DeviceInfo>> requestCallback) {
        this.<DeviceInfo>getIdsMap(DEVICES_ROUTE, devicesIds, requestCallback, new TypeToken<Map<String, DeviceInfo>>() {
        });
//        HttpUrl.Builder urlBuilder = serverUrl
//                .newBuilder()
//                .addPathSegment(DEVICES_ROUTE)
//                .addPathSegment("map");
//        Log.d("pttt", "Requesting devices from URL: " + urlBuilder.build());
//        Request.Builder request = new Request.Builder()
//                .url(urlBuilder.build())
//                .post(createRequestBody(devicesIds))
//                .build();
//
//        sendRequest(request, requestCallback, Collections.emptyMap(),
//                new TypeToken<Map<String, DeviceInfo>>() {
//                });
    }

    public void updateDevice(final String deviceId, UpdateDeviceDto update, final EmptyRequestCallback requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(DEVICES_ROUTE)
                .addPathSegment(deviceId);
        Log.d("pttt", "Requesting user from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .put(createRequestBody(update));
        sendRequest(request, requestCallback);
    }

    public void deleteDevice(final String deviceId, final String userId, final EmptyRequestCallback requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment("users")
                .addPathSegment(userId)
                .addPathSegment(DEVICES_ROUTE)
                .addPathSegment(deviceId);
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .delete();
        sendRequest(request, requestCallback);
    }
    //</editor-fold>

    //<editor-fold desc="ControlPanel API">
    public void createControlPanel(final CreateControlPanelDto createControlPanelDto, final String userId, final RequestCallback<ControlPanel> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment("users")
                .addPathSegment(userId)
                .addPathSegment(CONTROL_PANELS_ROUTE);
        Log.d("pttt", "Requesting device from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .post(createRequestBody(createControlPanelDto));
        sendRequest(request, requestCallback, new TypeToken<ControlPanel>() {
        });
    }

    public void getControlPanels(String[] panelsIds, RequestCallback<Map<String, ControlPanel>> requestCallback) {
        getIdsMap(CONTROL_PANELS_ROUTE, panelsIds, requestCallback, new TypeToken<Map<String, ControlPanel>>() {
        });
//        HttpUrl.Builder urlBuilder = serverUrl
//                .newBuilder()
//                .addPathSegment(CONTROL_PANELS_ROUTE)
//                .addPathSegment("map");
//        Log.d("pttt", "Requesting control panels from URL: " + urlBuilder.build());
//        Request.Builder request = new Request.Builder()
//                .url(urlBuilder.build())
//                .post(createRequestBody(panelsIds))
//                .build();
//
//        sendRequest(request, requestCallback, Collections.emptyMap(), new TypeToken<Map<String, ControlPanel>>() {
//        });
    }

    public void getControlPanel(String controlPanelId, RequestCallback<ControlPanel> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(CONTROL_PANELS_ROUTE)
                .addPathSegment(controlPanelId);
        Log.d("pttt", "Requesting control panel from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .get();
        sendRequest(request, requestCallback, new TypeToken<ControlPanel>() {
        });
    }

    public void updateControlPanel(UpdateControlPanelDto update, String controlPanelId, EmptyRequestCallback requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(CONTROL_PANELS_ROUTE)
                .addPathSegment(controlPanelId);
        Log.d("pttt", "Updating device from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .put(createRequestBody(update));
        sendRequest(request, requestCallback);
    }

    public void deleteControlPanel(final String controlPanelId, final String userId, final EmptyRequestCallback requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment("users")
                .addPathSegment(userId)
                .addPathSegment(CONTROL_PANELS_ROUTE)
                .addPathSegment(controlPanelId);
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .delete();
        sendRequest(request, requestCallback);
    }
    //</editor-fold>

    //<editor-fold desc="User API">
    public void createUser(final NewUser newUser, final RequestCallback<UserCreated> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment("users");
        Log.d("pttt", "Requesting new user from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .post(createRequestBody(newUser));
        sendRequest(request, requestCallback, new TypeToken<UserCreated>() {
        });
    }

    public void getUser(final String userId, final RequestCallback<User> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment("users")
                .addPathSegment(userId);
        Log.d("pttt", "Requesting user from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .get();
        sendRequest(request, requestCallback, new TypeToken<User>() {
        });
    }
    //</editor-fold>

    //<editor-fold desc="Topic API">

    public void createTopic(final CreateTopicDto topicDto, String deviceId, RequestCallback<TopicInfo> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(DEVICES_ROUTE)
                .addPathSegment(deviceId)
                .addPathSegment(TOPICS_ROUTE);
        Log.d("pttt", "Creating topic, URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .post(createRequestBody(topicDto));
        sendRequest(request, requestCallback, new TypeToken<TopicInfo>() {
        });
    }

    public void getTopic(String topicId, RequestCallback<TopicInfo> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(TOPICS_ROUTE)
                .addPathSegment(topicId);
        Log.d("pttt", "Requesting control panel from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .get();
        sendRequest(request, requestCallback, new TypeToken<TopicInfo>() {
        });
    }

    public void getTopics(String[] topicsIds, RequestCallback<Map<String, TopicInfo>> requestCallback) {
        getIdsMap(TOPICS_ROUTE, topicsIds, requestCallback, new TypeToken<Map<String, TopicInfo>>() {
        });
    }

    public void updateTopic(UpdateTopicDto update, String topicId, EmptyRequestCallback requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(TOPICS_ROUTE)
                .addPathSegment(topicId);
        Log.d("pttt", "Requesting user from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .put(createRequestBody(update));
        sendRequest(request, requestCallback);
    }

    public void deleteTopic(String topicId, String deviceId, EmptyRequestCallback requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(DEVICES_ROUTE)
                .addPathSegment(deviceId)
                .addPathSegment(TOPICS_ROUTE)
                .addPathSegment(topicId);
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .delete();
        sendRequest(request, requestCallback);
    }
    //</editor-fold>


    //<editor-fold desc="PositionedComponent API">
    public void createPositionedComponent(CreatePositionedComponent createPositionedComponent, String controlPanelId, RequestCallback<PositionedComponent> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(CONTROL_PANELS_ROUTE)
                .addPathSegment(controlPanelId)
                .addPathSegment(COMPONENTS_ROUTE);
        Log.d("pttt", "Creating positioned component, URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .post(createRequestBody(createPositionedComponent));
        sendRequest(request, requestCallback, new TypeToken<PositionedComponent>() {
        });
    }

    public void getPositionedComponent(String componentId, RequestCallback<PositionedComponent> requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(POSITIONED_COMPONENTS_ROUTE)
                .addPathSegment(componentId);
        Log.d("pttt", "Requesting control panel from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .get();
        sendRequest(request, requestCallback, new TypeToken<PositionedComponent>() {
        });
    }

    public void getPositionedComponents(String[] ids, RequestCallback<Map<String, PositionedComponent>> requestCallback) {
        getIdsMap(POSITIONED_COMPONENTS_ROUTE, ids, requestCallback, new TypeToken<Map<String, PositionedComponent>>() {
        });
    }

    public void updatePositionedComponent(UpdatePositionedComponent update, String componentId, EmptyRequestCallback requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(POSITIONED_COMPONENTS_ROUTE)
                .addPathSegment(componentId);
        Log.d("pttt", "Requesting user from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .put(createRequestBody(update));
        sendRequest(request, requestCallback);
    }

    public void deletePositionedComponent(String componentId, String controlPanelId, EmptyRequestCallback requestCallback) {
        final HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(CONTROL_PANELS_ROUTE)
                .addPathSegment(controlPanelId)
                .addPathSegment(POSITIONED_COMPONENTS_ROUTE)
                .addPathSegment(componentId);
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .delete();
        sendRequest(request, requestCallback);
    }
    //</editor-fold>

    //<editor-fold desc="Helper functions">
    private <T> void getIdsMap(String collection, String[] ids, RequestCallback<Map<String, T>> requestCallback,
                               TypeToken<Map<String, T>> responseType) {
        HttpUrl.Builder urlBuilder = serverUrl
                .newBuilder()
                .addPathSegment(collection)
                .addPathSegment("map");
        Log.d("pttt", "Requesting " + collection + " map from URL: " + urlBuilder.build());
        final Request.Builder request = new Request.Builder()
                .url(urlBuilder.build())
                .post(createRequestBody(ids));

        sendRequest(request, requestCallback, responseType);
    }

    private void sendRequest(final Request.Builder request, final EmptyRequestCallback requestCallback) {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getIdToken(true).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                requestCallback.onFailure(new TokenGenerationException());
                return;
            }
            request.header("Authorization", "Bearer " + task.getResult().getToken());
            request.header("User-Agent", "ConnectX/Android");
            httpClient.newCall(request.build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requestCallback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (!response.isSuccessful()) {
                        requestCallback.onFailure(response.code());
                        return;
                    }

                    requestCallback.onSuccess(response.code());
                }
            });
        });
    }

    private <T> void sendRequest(Request.Builder request, RequestCallback<T> requestCallback, TypeToken<T> responseType) {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getIdToken(true).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                requestCallback.onFailure(new TokenGenerationException());
                return;
            }
            request.header("Authorization", "Bearer " + task.getResult().getToken());
            request.header("User-Agent", "ConnectX/Android");
            httpClient.newCall(request.build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requestCallback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        requestCallback.onFailure(response.code());
                        return;
                    }
                    final String body = response.body().string();
                    System.out.println("Response: " + body);
                    final T data = gson.fromJson(body, responseType.getType());
                    requestCallback.onSuccess(data, response.code());
                }
            });
        });
    }

    private RequestBody createRequestBody(Object obj) {
        return RequestBody.create(MediaType.get("application/json"), gson.toJson(obj));
    }
    //</editor-fold>

    //<editor-fold desc="Callback interfaces">
    public interface EmptyRequestCallback {

        void onSuccess(final int statusCode);

        void onFailure(final Throwable throwable);

        void onFailure(final int statusCode);
    }

    public interface RequestCallback<T> extends EmptyRequestCallback {
        void onSuccess(final T data, final int statusCode);
    }
    //</editor-fold>

    // Source: https://stackoverflow.com/a/49455438
    private final static class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull final Chain chain) throws IOException {
            try (final Response response = chain.proceed(chain.request())) {
                final CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(15, TimeUnit.MINUTES) // 15 minutes cache
                        .build();

                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", cacheControl.toString())
                        .build();
            }
        }
    }

    private final static class TokenGenerationException extends Exception {
        public TokenGenerationException() {
            super("Unable to get ID token for the current user!");
        }
    }
}
