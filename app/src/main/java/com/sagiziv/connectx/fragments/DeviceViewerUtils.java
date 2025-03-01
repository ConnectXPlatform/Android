package com.sagiziv.connectx.fragments;

import androidx.annotation.NonNull;

import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.Status;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DeviceViewerUtils {
    private static final String DateFormatPattern = "dd/MM/yyyy HH:mm";

    public static String getDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateFormatPattern, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat.format(date);
    }

    public static int getConnectionStatusStringResource(@NonNull Status status){
        return status.isConnected() ? R.string.online_status : R.string.offline_status;
    }

    public static int getConnectionDateStatusStringResource(@NonNull Status status){
        return status.isConnected() ? R.string.online_since : R.string.offline_since;
    }
}
