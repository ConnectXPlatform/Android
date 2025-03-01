package com.sagiziv.connectx.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.sagiziv.connectx.R;

public class PromptFragment extends Fragment {
    private TextView promptTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prompt, container, false);
        findViews(view);
        return view;
    }

    public void setText(CharSequence text) {
        promptTextView.setText(text);
    }

    public void setText(@StringRes int text) {
        promptTextView.setText(text);
    }

    private void findViews(@NonNull View root) {
        promptTextView = root.findViewById(R.id.prompt_LBL_text);
    }
}
