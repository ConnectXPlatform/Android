package com.sagiziv.connectx;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;

import com.sagiziv.connectx.utils.AndroidUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FeedbackHandler {
    private final Context context;
    private final Vibrator vibrator;
    private MediaPlayer mediaPlayer;

    private static FeedbackHandler mySignal;

    private FeedbackHandler(Context context) {
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static void init(Context context) {
        if (mySignal == null) {
            mySignal = new FeedbackHandler(context.getApplicationContext());
        }
    }

    public static FeedbackHandler getInstance() {
        return mySignal;
    }


    public void vibrate(long milliseconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(milliseconds);
        }
    }

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToastDuration {
    }

    public void toast(String message, @ToastDuration int toastLength) {
        AndroidUtils.runOnMainThread(() -> {
            try {
                Toast.makeText(context, message, toastLength).show();
            } catch (IllegalStateException ignored) {
            }
        });
    }

    public void toast(@StringRes int message, @ToastDuration int toastLength) {
        AndroidUtils.runOnMainThread(() -> {
            try {
                Toast.makeText(context, message, toastLength).show();
            } catch (IllegalStateException ignored) {
            }
        });
    }

    public void toast(String message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    public void toast(@StringRes int message) {
        toast(message, Toast.LENGTH_SHORT);
    }

    public void playAudio(@RawRes int audioFile) {
        // Stop the previous media player
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(context, audioFile);
        mediaPlayer.start();
        // Make sure to dispose of the media player after it finished playing.
        mediaPlayer.setOnCompletionListener(mp -> {
            mediaPlayer.release();
            mediaPlayer = null;
        });
    }
}
