package me.devkevin.practice.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * Copyright 29/04/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ConfigurationSerializableTypeAdapter implements JsonDeserializer<ConfigurationSerializable>, JsonSerializer<ConfigurationSerializable> {

    private static final Type type = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();

    private static ConfigurationSerializable deserializeObject(Map<String, Object> deserialized) {
        LinkedHashMap<String, Object> conversion = new LinkedHashMap<>(deserialized.size());
        for (Map.Entry<String, Object> entry : deserialized.entrySet()) {
            if (entry.getValue() instanceof Map && ((Map) (entry.getValue())).containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                Map<?, ?> raw = (Map<?, ?>) entry.getValue();
                Map<String, Object> typed = new LinkedHashMap<>(raw.size());
                for (Map.Entry<?, ?> child : raw.entrySet()) {
                    typed.put(child.getKey().toString(), fixObject(child.getKey().toString(), child.getValue()));
                }
                conversion.put(entry.getKey(), deserializeObject(typed));
            } else {
                conversion.put(entry.getKey(), fixObject(entry.getKey(), entry.getValue()));
            }
        }
        return ConfigurationSerialization.deserializeObject(conversion);
    }

    private static Object fixObject(String key, Object object) {
        if (object instanceof Double) {
            return ((Double) object).intValue();
        }
        return object;
    }

    @Override
    public ConfigurationSerializable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            LinkedHashMap<String, Object> deserialized = context.deserialize(json, type);
            if (deserialized.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                return deserializeObject(deserialized);
            } else {
                Method method = ((Class<?>) typeOfT).getDeclaredMethod("deserialize", Map.class);
                return (ConfigurationSerializable) method.invoke(null, deserialized);
            }
        } catch (Exception e) {
            System.out.println("Currently In Block " + json.toString());
            throw new JsonParseException("Could not deserialize " + typeOfT + ".", e);
        }
    }

    @Override
    public JsonElement serialize(ConfigurationSerializable src, Type typeOfSrc, JsonSerializationContext context) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(src.getClass()));
        values.putAll(src.serialize());
        return context.serialize(values);
    }
}

