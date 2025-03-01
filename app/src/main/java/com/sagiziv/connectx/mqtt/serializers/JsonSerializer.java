package com.sagiziv.connectx.mqtt.serializers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class JsonSerializer implements Serializer {
    private final Gson gson = new Gson();

    @Override
    public String Serialize(Object data) {
        return gson.toJson(data);
    }

    @Override
    public <T> T Deserialize(String data, Type type) {
        return gson.fromJson(data, type);
    }
}
