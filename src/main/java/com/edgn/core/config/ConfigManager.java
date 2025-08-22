package com.edgn.core.config;

import com.edgn.Main;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigManager {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SaveField {}

    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(Main.MOD_ID);

    private static final Map<Class<?>, Gson> GSON_ADAPTERS = new HashMap<>();

    private static final Map<String, Object> LOADED_DATA = new HashMap<>();

    private static final String JSON_EXTENSION = ".json";

    public static void init() {
        try {
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }
        } catch (Exception e) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().error("Failed to initialize ConfigManager: " + e.getMessage(), true);
        }
    }

    public static <T> boolean saveList(String filename, List<T> list, Class<T> type) {
        try {
            Path filePath = CONFIG_DIR.resolve(filename + JSON_EXTENSION);

            Gson gson = getGsonForType(type);

            String json = gson.toJson(list);

            try (Writer writer = Files.newBufferedWriter(filePath)) {
                writer.write(json);
            }

            LOADED_DATA.put(filename, new ArrayList<>(list));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> boolean saveObject(String filename, T object, Class<T> type) {
        try {
            Path filePath = CONFIG_DIR.resolve(filename + JSON_EXTENSION);

            Gson gson = getGsonForType(type);

            String json = gson.toJson(object);

            try (Writer writer = Files.newBufferedWriter(filePath)) {
                writer.write(json);
            }

            LOADED_DATA.put(filename, object);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadList(String filename, Class<T> type, Supplier<List<T>> defaultSupplier) {

        if (LOADED_DATA.containsKey(filename) && LOADED_DATA.get(filename) instanceof List) {
            return (List<T>) LOADED_DATA.get(filename);
        }

        Path filePath = CONFIG_DIR.resolve(filename + JSON_EXTENSION);

        if (!Files.exists(filePath)) {
            List<T> defaultList = defaultSupplier.get();
            LOADED_DATA.put(filename, defaultList);
            return defaultList;
        }

        try {

            String fileContent = Files.readString(filePath);

            if (fileContent.isEmpty()) {
                List<T> defaultList = defaultSupplier.get();
                LOADED_DATA.put(filename, defaultList);
                return defaultList;
            }

            Gson gson = getGsonForType(type);
            Type listType = TypeToken.getParameterized(List.class, type).getType();

            try (Reader reader = Files.newBufferedReader(filePath)) {
                List<T> list = gson.fromJson(reader, listType);

                if (list == null) {
                    list = defaultSupplier.get();
                }

                LOADED_DATA.put(filename, list);
                return list;
            }
        } catch (Exception e) {
            List<T> defaultList = defaultSupplier.get();
            LOADED_DATA.put(filename, defaultList);
            return defaultList;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T loadObject(String filename, Class<T> type, Supplier<T> defaultSupplier) {

        if (LOADED_DATA.containsKey(filename)) {
            Object cached = LOADED_DATA.get(filename);
            if (type.isInstance(cached)) {
                return (T) cached;
            }
        }

        Path filePath = CONFIG_DIR.resolve(filename + JSON_EXTENSION);

        if (!Files.exists(filePath)) {
            T defaultObject = defaultSupplier.get();
            LOADED_DATA.put(filename, defaultObject);
            return defaultObject;
        }

        try {

            String fileContent = Files.readString(filePath);

            if (fileContent.isEmpty()) {
                T defaultObject = defaultSupplier.get();
                LOADED_DATA.put(filename, defaultObject);
                return defaultObject;
            }

            Gson gson = getGsonForType(type);

            try (Reader reader = Files.newBufferedReader(filePath)) {
                T object = gson.fromJson(reader, type);

                if (object == null) {
                    object = defaultSupplier.get();
                }

                LOADED_DATA.put(filename, object);
                return object;
            }
        } catch (Exception e) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().error("Failed to load object from" + filename, true);
            T defaultObject = defaultSupplier.get();
            LOADED_DATA.put(filename, defaultObject);
            return defaultObject;
        }
    }

    private static <T> Gson getGsonForType(Class<T> type) {
        return GSON_ADAPTERS.computeIfAbsent(type, ConfigManager::createGsonAdapter);
    }

    private static <T> Gson createGsonAdapter(Class<T> type) {
        GsonBuilder builder = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation();

        return builder.create();
    }

    public static <T> void registerGsonAdapter(Class<T> type, Gson gson) {
        GSON_ADAPTERS.put(type, gson);
    }
}