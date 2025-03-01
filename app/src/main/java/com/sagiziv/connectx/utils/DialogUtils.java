package com.sagiziv.connectx.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.sagiziv.connectx.R;

import java.util.function.Consumer;

public final class DialogUtils {
    private static int white = -1;
    private final AlertDialog.Builder builder;

    public DialogUtils(Context context) {
        builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogStyle);
        builder.setCancelable(false);
    }

    public static DialogUtils createDialog(Context context) {
        return new DialogUtils(context);
    }

    public DialogUtils withTitle(CharSequence title) {
        builder.setTitle(title);
        return this;
    }

    public DialogUtils withTitle(@StringRes int title) {
        builder.setTitle(title);
        return this;
    }

    public DialogUtils withIcon(@DrawableRes int icon) {
        builder.setIcon(icon);
        return this;
    }

    public DialogUtils withTextContent(String text) {
        builder.setMessage(text);
        return this;
    }

    public DialogUtils withTextContent(@StringRes int text) {
        builder.setMessage(text);
        return this;
    }

    public DialogUtils withLayoutContent(@LayoutRes int layoutResource) {
        builder.setView(layoutResource);
        return this;
    }

    public DialogUtils withPositiveButton(String text, Consumer<AlertDialog> onClick) {
        builder.setPositiveButton(text, (dialog, which) -> onClick.accept((AlertDialog) dialog));
        return this;
    }

    public DialogUtils withPositiveButton(@StringRes int text, Consumer<AlertDialog> onClick) {
        builder.setPositiveButton(text, (dialog, which) -> onClick.accept((AlertDialog) dialog));
        return this;
    }

    public DialogUtils withNegativeButton(String text, Consumer<AlertDialog> onClick) {
        builder.setNegativeButton(text, (dialog, which) -> onClick.accept((AlertDialog) dialog));
        return this;
    }

    public DialogUtils withNegativeButton(@StringRes int text, Consumer<AlertDialog> onClick) {
        builder.setNegativeButton(text, (dialog, which) -> onClick.accept((AlertDialog) dialog));
        return this;
    }

    public DialogUtils isCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
        return this;
    }

    @NonNull
    public AlertDialog getDialog() {
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> postBuildProcessing((AlertDialog) d, builder.getContext()));
        return dialog;
    }

    public void showDialog() {
        AlertDialog dialog = builder.show();
        postBuildProcessing(dialog, builder.getContext());
    }

    private static void postBuildProcessing(@NonNull AlertDialog dialog, Context context) {
        if (white == -1)
            white = ColorUtils.getColor(context, R.color.white);

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (negativeButton != null)
            negativeButton.setTextColor(white);
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton != null)
            positiveButton.setTextColor(white);
    }

}
