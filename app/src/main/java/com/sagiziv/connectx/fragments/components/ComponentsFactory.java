package com.sagiziv.connectx.fragments.components;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ComponentsFactory {

    public static Map<String, Class<? extends DraggableComponentFragment<?>>> fragmentsMap;

    public static DraggableComponentFragment<?> getComponentFragmentInstance(@NonNull String componentId) {
        Class<? extends DraggableComponentFragment<?>> clazz = fragmentsMap.getOrDefault(componentId.toLowerCase(), null);
        if (clazz == null) return null;
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        fragmentsMap = new HashMap<>();
        fragmentsMap.put("label", LabelComponentFragment.class);
        fragmentsMap.put("button", ButtonComponentFragment.class);
        fragmentsMap.put("temps", TemperaturesViewerComponentFragment.class);
        fragmentsMap.put("data-request", DataRequestComponentFragment.class);
        fragmentsMap.put("button.power", PowerButtonComponentFragment.class);
    }

    public static Collection<String> getComponentsIds() {
        return fragmentsMap.keySet();
    }
}
