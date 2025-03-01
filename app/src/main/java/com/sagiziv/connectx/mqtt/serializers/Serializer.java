package com.sagiziv.connectx.mqtt.serializers;

import java.lang.reflect.Type;

public interface Serializer {
    String Serialize(Object data);

    <T> T Deserialize(String data, Type type);
}
