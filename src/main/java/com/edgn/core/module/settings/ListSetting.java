package com.edgn.core.module.settings;

import com.edgn.core.minecraft.ui.screens.modules.settings.ISettingsScreen;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListSetting<T> extends Setting<List<T>> {
    private final Function<String, T> stringToValueParser;
    private final Function<T, String> valueToStringConverter;
    private static final Gson GSON = new Gson();

    public ListSetting(String name, String description, List<T> defaultValue, Function<String, T> stringToValueParser, Function<T, String> valueToStringConverter) {
        super(name, description, defaultValue != null ? defaultValue : new ArrayList<>());
        this.stringToValueParser = stringToValueParser;
        this.valueToStringConverter = valueToStringConverter;
    }
    public ListSetting(String name, String description, List<T> defaultValue, Function<String, T> stringToValueParser) {
        this(name, description, defaultValue, stringToValueParser, T::toString);
    }

    public Function<String, T> getStringToValueParser() { return stringToValueParser; }
    public Function<T, String> getValueToStringConverter() { return valueToStringConverter; }

    @Override
    public JsonElement toJsonElement() {
        return GSON.toJsonTree(getValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fromJsonElement(JsonElement element) {
        if (element == null || !element.isJsonArray()) return;

        List<T> defaultValue = getDefaultValue();
        if (defaultValue.isEmpty()) {
            try {
                List<T> loadedList = GSON.fromJson(element, this.getValue().getClass());
                setValue(loadedList);
            } catch (Exception e) { /* échec du cast */ }
            return;
        }

        Class<?> contentType = defaultValue.getFirst().getClass();
        JsonArray jsonArray = element.getAsJsonArray();
        List<T> newList = new ArrayList<>();

        for (JsonElement itemElement : jsonArray) {
            T item = (T) GSON.fromJson(itemElement, contentType);
            newList.add(item);
        }
        setValue(newList);
    }
}