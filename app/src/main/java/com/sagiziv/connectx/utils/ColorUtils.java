package com.sagiziv.connectx.utils;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public final class ColorUtils {

    private static final Map<Integer, Integer> colorsMap;

    static {
        colorsMap = new HashMap<>(5);
    }

    @ColorInt
    public static int getColor(Context context, @ColorRes int colorId){
        return colorsMap.computeIfAbsent(colorId, id -> ContextCompat.getColor(context, id));
    }
}
