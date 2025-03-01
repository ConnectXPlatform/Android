package com.sagiziv.connectx.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

public final class AndroidUtils {
    private static final Handler MainThreadHandler = new Handler(Looper.getMainLooper());

    @Nullable
    public static String getBluetoothName(@NonNull final Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
    }

    public static String getVersionInfo() {
        return getAndroidVersionName(Build.VERSION.SDK_INT) + " (" + Build.VERSION.RELEASE + ")";
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    @NonNull
    public static String generateDeviceDescription() {
        return "Android version: " + getVersionInfo() + '\n' +
                "Manufacturer: " + getManufacturer() + '\n' +
                "Model: " + getModel();
    }

    public static void runOnMainThread(Runnable runnable) {
        MainThreadHandler.post(runnable);
    }

    public static Drawable tintIcon(@NonNull Drawable icon, @ColorInt int color) {
        // Make sure that the drawable supports tinting.
        Drawable wrappedDrawable = DrawableCompat.wrap(icon).mutate();
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    private static String getAndroidVersionName(final int versionCode) {
        String versionName = "Unknown";

        switch (versionCode) {
            case Build.VERSION_CODES.BASE:
                versionName = "Base";
                break;
            case Build.VERSION_CODES.BASE_1_1:
                versionName = "Base 1.1";
                break;
            case Build.VERSION_CODES.CUPCAKE:
                versionName = "Cupcake";
                break;
            case Build.VERSION_CODES.DONUT:
                versionName = "Donut";
                break;
            case Build.VERSION_CODES.ECLAIR:
            case Build.VERSION_CODES.ECLAIR_0_1:
            case Build.VERSION_CODES.ECLAIR_MR1:
                versionName = "Eclair";
                break;
            case Build.VERSION_CODES.FROYO:
                versionName = "Froyo";
                break;
            case Build.VERSION_CODES.GINGERBREAD:
            case Build.VERSION_CODES.GINGERBREAD_MR1:
                versionName = "Gingerbread";
                break;
            case Build.VERSION_CODES.HONEYCOMB:
            case Build.VERSION_CODES.HONEYCOMB_MR1:
            case Build.VERSION_CODES.HONEYCOMB_MR2:
                versionName = "Honeycomb";
                break;
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
                versionName = "Ice Cream Sandwich";
                break;
            case Build.VERSION_CODES.JELLY_BEAN:
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
                versionName = "Jelly Bean";
                break;
            case Build.VERSION_CODES.KITKAT:
            case Build.VERSION_CODES.KITKAT_WATCH:
                versionName = "KitKat";
                break;
            case Build.VERSION_CODES.LOLLIPOP:
            case Build.VERSION_CODES.LOLLIPOP_MR1:
                versionName = "Lollipop";
                break;
            case Build.VERSION_CODES.M:
                versionName = "Marshmallow";
                break;
            case Build.VERSION_CODES.N:
            case Build.VERSION_CODES.N_MR1:
                versionName = "Nougat";
                break;
            case Build.VERSION_CODES.O:
            case Build.VERSION_CODES.O_MR1:
                versionName = "Oreo";
                break;
            case Build.VERSION_CODES.P:
                versionName = "Pie";
                break;
            case Build.VERSION_CODES.Q:
                versionName = "Android 10";
                break;
            case Build.VERSION_CODES.R:
                versionName = "Android 11";
                break;
            case Build.VERSION_CODES.S:
                versionName = "Android 12";
                break;
            case Build.VERSION_CODES.S_V2:
                versionName = "Android 12L";
                break;
            case Build.VERSION_CODES.TIRAMISU:
                versionName = "Android 13";
                break;
        }

        return versionName;
    }
}
