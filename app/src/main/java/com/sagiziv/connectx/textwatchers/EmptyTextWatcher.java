package com.sagiziv.connectx.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.StringRes;

import com.sagiziv.connectx.R;

import java.util.function.Consumer;

public class EmptyTextWatcher implements TextWatcher {
    private final EditText editText;
    private final Consumer<Boolean> stateChanged;
    private final CharSequence errorMessage;

    public EmptyTextWatcher(EditText editText, Consumer<Boolean> stateChanged) {
        this.editText = editText;
        this.stateChanged = stateChanged;
        this.errorMessage = editText.getResources().getText(R.string.empty_input);
    }

    public EmptyTextWatcher(EditText editText, Consumer<Boolean> stateChanged, @StringRes int errorMessage) {
        this.editText = editText;
        this.stateChanged = stateChanged;
        this.errorMessage = editText.getResources().getText(errorMessage);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().trim().length() == 0) {
            editText.setError(errorMessage);
            stateChanged.accept(false);
        } else {
            editText.setError(null);
            stateChanged.accept(true);
        }
    }
}
