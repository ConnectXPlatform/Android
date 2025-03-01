package com.sagiziv.connectx.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sagiziv.connectx.Database;
import com.sagiziv.connectx.DevicesHandler;
import com.sagiziv.connectx.MSPV3;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.bundlewrappers.HomeScreenBundleWrapper;
import com.sagiziv.connectx.dto.CreateDeviceDto;
import com.sagiziv.connectx.dto.DeviceInfo;
import com.sagiziv.connectx.dto.NewUser;
import com.sagiziv.connectx.dto.User;
import com.sagiziv.connectx.dto.UserCreated;
import com.sagiziv.connectx.mqtt.MqttHandler;
import com.sagiziv.connectx.utils.AndroidUtils;
import com.sagiziv.connectx.utils.ColorUtils;
import com.sagiziv.connectx.utils.DialogUtils;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Collections;

public class LoginActivity extends FullScreenActivity {
    static final String DEVICE_ID_KEY = "DEVICE_ID";
    static final String SERVER_URL_KEY = "SERVER_URL";
    private TextView statusLabel;
    private MqttHandler mqttHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        showSplashScreenAnimation(this::showInputDialog);
//        if (FirebaseAuth.getInstance().getCurrentUser() != null)
//            FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).addOnCompleteListener(runnable -> {
//                if(runnable.isSuccessful())
//                    Log.d("pttt", runnable.getResult().getToken());
//            });
    }

    private void findViews() {
        statusLabel = findViewById(R.id.login_LBL_status);
    }

    private void showSplashScreenAnimation(final Runnable postAnimationAction) {
        // Source: https://youtu.be/JLIFqqnSNmg
        final Animation top = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        final Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        final View icon = findViewById(R.id.login_IMG_icon);
        final View appName = findViewById(R.id.login_LBL_title);
        final View indicator = findViewById(R.id.login_PRG_indicator);
        icon.setAnimation(top);
        appName.setAnimation(top);

        indicator.setAnimation(fade);
        statusLabel.setAnimation(fade);

        // Wait for the longest animation to end
        final Animation longest = top.getDuration() > fade.getDuration() ? top : fade;
        longest.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {

            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                postAnimationAction.run();
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {

            }
        });
    }

    private void showInputDialog() {
        final AlertDialog dialog = DialogUtils.createDialog(this)
                .withTitle("Enter server details")
                .withLayoutContent(R.layout.dialog_server_parameters_input)
                .withPositiveButton("OK", alertDialog -> {
                    // Get the layout's views
                    final EditText urlInputField = alertDialog.findViewById(R.id.server_dialog_LBL_url);
                    final EditText dbPortInputField = alertDialog.findViewById(R.id.server_dialog_LBL_db_port);
                    final EditText mqttPortInputField = alertDialog.findViewById(R.id.server_dialog_LBL_mqtt_port);

                    // Parse the parameters
                    final String serverUrl = urlInputField.getText().toString();
                    final int dbPort = Integer.parseInt(dbPortInputField.getText().toString());
                    final int mqttPort = Integer.parseInt(mqttPortInputField.getText().toString());
                    Database.initialize(serverUrl, dbPort);
                    MqttHandler.initialize(serverUrl, mqttPort);
                    mqttHandler = MqttHandler.getInstance();

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
//                        showLoginScreen();
                        FirebaseAuth.getInstance().signInWithEmailAndPassword("sagiziv3@gmail.com", "q1w2e3r4t5")
                                .addOnCompleteListener(runnable -> {
                                    processUser(runnable.getResult().getUser());
                                });
                    } else {
                        processUser(user);
                    }
                })
                .getDialog();

        dialog.show();
        final int white = ColorUtils.getColor(this, R.color.white);
        final int grey = ColorUtils.getColor(this, R.color.grey);
        // Make sure the button is disabled by default.
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);
        positiveButton.setTextColor(grey);

        // Show error message for wrong input
        final EditText urlInputField = dialog.findViewById(R.id.server_dialog_LBL_url);
        urlInputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                positiveButton.setEnabled(true);
                positiveButton.setTextColor(white);
                final String serverUrl = s.toString();
                if (!Patterns.DOMAIN_NAME.matcher(serverUrl).matches()) {
                    urlInputField.setError("Invalid URL!");
                    positiveButton.setEnabled(false);
                    positiveButton.setTextColor(grey);
                }
            }
        });
        urlInputField.setText(MSPV3.getInstance().readString(SERVER_URL_KEY, ""));
    }

    private void showLoginScreen() {
        statusLabel.setText("Logging/Signing in");
        final Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build()
                ))
                .setLogo(R.drawable.ic_app_icon)
                .setTheme(R.style.Theme_ConnectX)
                .setIsSmartLockEnabled(false)
                .setAlwaysShowSignInMethodScreen(true)
                .build();

        signInLauncher.launch(signInIntent);
    }

    private void processUser(@NonNull final FirebaseUser user) {
        final Database database = Database.getInstance();
        statusLabel.setText("Finding user on the server");
        database.getUser(user.getUid(), new Database.RequestCallback<User>() {
            @Override
            public void onSuccess(User data, int statusCode) {
                handleUserLogin(data);
            }

            @Override
            public void onSuccess(int statusCode) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                runOnUiThread(() -> {
                    DialogUtils.createDialog(LoginActivity.this)
                            .isCancelable(false)
                            .withTitle("Error occurred")
                            .withTextContent("Error connecting to the server: " + throwable.getMessage())
                            .withPositiveButton("Exit", dialog -> finish())
                            .getDialog()
                            .show();
                });
            }

            @Override
            public void onFailure(int statusCode) {
                // If the user is not in the DB, it is probably a new user
                if (statusCode == 404) {
                    createUser(user.getUid(), user.getDisplayName());
                }
            }
        });
    }

    private void createUser(final String userId, @Nullable final String userName) {
        final String bluetoothName = AndroidUtils.getBluetoothName(this);
        final String deviceDescription = AndroidUtils.generateDeviceDescription();

        // The Bluetooth name is usually the device's name (unless the user changed it).
        final String deviceName = bluetoothName != null && bluetoothName.length() > 0
                ? bluetoothName
                : AndroidUtils.getModel();
        NewUser newUser = new NewUser()
                .setUserId(userId)
                .setUserName(userName)
                .setDeviceDescription(deviceDescription)
                .setDeviceName(deviceName);
        statusLabel.setText("Creating user on the server");
        Database.getInstance()
                .createUser(newUser, new Database.RequestCallback<UserCreated>() {
                    @Override
                    public void onSuccess(final UserCreated data, final int statusCode) {
                        // Save this device's id.
                        MSPV3.getInstance().saveString(getDeviceKey(data.getUser()), data.getDevice().getId());
                        handleUserLogin(data.getUser(), data.getDevice());
                    }

                    @Override
                    public void onSuccess(final int statusCode) {
                    }

                    @Override
                    public void onFailure(final Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }

                    @Override
                    public void onFailure(final int statusCode) {
                        // 409 = Conflict.
                        // We aren't suppose to get here.
                        if (statusCode == 409) {
                            Log.d("pttt", "User already exist?!");
                            //noinspection ConstantConditions
                            processUser(FirebaseAuth.getInstance().getCurrentUser());
                        }
                    }
                });
    }

    private String getDeviceKey(User user) {
        return user.getId() + "_" + DEVICE_ID_KEY;
    }

    private void handleUserLogin(User user) {
        final String deviceId = MSPV3.getInstance().readString(getDeviceKey(user), "");
        Log.d("pttt", "Device id is: " + deviceId);
        Database.RequestCallback<DeviceInfo> requestCallback = new Database.RequestCallback<DeviceInfo>() {
            @Override
            public void onSuccess(DeviceInfo data, int statusCode) {
                MSPV3.getInstance().saveString(getDeviceKey(user), data.getId());
                handleUserLogin(user, data);
            }

            @Override
            public void onSuccess(int statusCode) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onFailure(int statusCode) {
                if (statusCode == 404)
                    Log.d("pttt", "Unable to find device's id!");
            }
        };
        // Assume nre device
        if (deviceId.isEmpty()) {
            final String bluetoothName = AndroidUtils.getBluetoothName(this);
            final String deviceDescription = AndroidUtils.generateDeviceDescription();

            // The Bluetooth name is usually the device's name (unless the user changed it).
            final String deviceName = bluetoothName != null && bluetoothName.length() > 0
                    ? bluetoothName
                    : AndroidUtils.getModel();
            Database.getInstance()
                    .createDevice(new CreateDeviceDto()
                            .setDescription(deviceDescription)
                            .setName(deviceName), user.getId(), requestCallback);
            return;
        }
        statusLabel.setText("Checking device");
        Database.getInstance()
                .getDevice(deviceId, requestCallback);
    }

    private void handleUserLogin(@NonNull User user, @NonNull DeviceInfo device) {
        statusLabel.setText("Connecting to server...");
        try {
            mqttHandler.connect(user.getId(), device.getId(),
                    () -> {
                        MSPV3.getInstance().saveString(SERVER_URL_KEY, mqttHandler.getServerUrl());
                        DevicesHandler.initialize();
                        Intent intent = new Intent(this, HomeScreenActivity.class);
                        HomeScreenBundleWrapper wrapper = new HomeScreenBundleWrapper(new Bundle());
                        wrapper.setUser(user);
                        wrapper.setDeviceInfo(device);
                        intent.putExtras(wrapper.getBundle());
                        intent.putExtra("user", user);
                        intent.putExtra("device", device);
                        startActivity(intent);
                        finish();
                    });
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
//        Intent intent = new Intent(this, HomeScreenActivity.class);
//        HomeScreenBundleWrapper wrapper = new HomeScreenBundleWrapper(new Bundle());
//        wrapper.setUser(user);
//        wrapper.setDeviceInfo(device);
//        intent.putExtras(wrapper.getBundle());
//        startActivity(intent);
//        finish();
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResult result) -> {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    processUser(user);
                    return;
                }

                DialogUtils.createDialog(this)
                        .withTitle("Please login to your account")
                        .withIcon(R.drawable.ic_error)
                        .withTextContent("You must log in to your account or create one to use the application")
                        .withPositiveButton("Log in", dialog -> showLoginScreen())
                        .withNegativeButton("Exit", dialog -> finish())
                        .showDialog();

//                final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyle)
//                        .setTitle("Please enter server details")
//                        .setIcon(R.drawable.ic_error)
//                        .setMessage("You must log in to your account or create one to use the application")
//                        .setPositiveButton("Log in", (dialog, which) -> showLoginScreen())
//                        .setNegativeButton("Exit", (dialog, which) -> finish())
//                        .setCancelable(false);
//                AlertDialog dialog = builder.show();
//                int white = ContextCompat.getColor(this, R.color.white);
//                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(white);
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(white);
            });
}