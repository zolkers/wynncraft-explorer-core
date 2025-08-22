package com.edgn.wynncraft.api;

import com.edgn.Main;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public enum MetadataParser {
    INSTANCE;

    public List<String> getIdentifications(){
        try {
            Gson gson = new Gson();

            JsonObject metadata = gson.fromJson(Main.WYNNCRAFT_FULL_METADATA, JsonObject.class);

            JsonArray identificationsArray = metadata.getAsJsonArray("identifications");

            List<String> identifications = new ArrayList<>();

            for (int i = 0; i < identificationsArray.size(); i++) {
                identifications.add(identificationsArray.get(i).getAsString());
            }
            return identifications;
        } catch (Exception e) {
            Main.LOGGER.error("Could not parse wynncraft's metadata API{}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public String getIdentificationsAsString() {
        List<String> identifications = getIdentifications();
        return String.join(", ", identifications);
    }
}
