package com.edgn.core.wynncraft.api;

import com.edgn.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiUtils {
    private static final int TIMEOUT = 10000;

    public static String getStringFromURL(String url) throws Exception {
        StringBuilder response = new StringBuilder();
        URL turl = new URI(url).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) turl.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);

        try {
            connection.connect();
            if (connection.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
            } else {
                throw new Exception("HTTP error code: " + connection.getResponseCode());
            }
        } finally {
            connection.disconnect();
        }
        return response.toString();
    }

    public static String getWynncraftDatabase() throws Exception {
        String apiUrl = "https://api.wynncraft.com/v3/item/database?fullResult";
        return getStringFromURL(apiUrl);
    }

    public static String getWynncraftMetadata() throws Exception {
        String apiUrl = "https://api.wynncraft.com/v3/item/metadata";
        return getStringFromURL(apiUrl);
    }

    public static String getWynncraftTree(String wynnClass) throws Exception {
        String apiUrl = "https://api.wynncraft.com/v3/ability/tree/" + wynnClass;
        return getStringFromURL(apiUrl);
    }

    public static String getWynncraftTreePlayerMap(String wynnClass) throws Exception {
        String apiUrl = "https://api.wynncraft.com/v3/ability/map/" + wynnClass;
        return getStringFromURL(apiUrl);
    }

    public static String getWynncraftPlayerStats(String uuid) throws Exception {
        String apiUrl = "https://api.wynncraft.com/v3/player/" + uuid + "?fullResult";
        Main.OVERLAY_MANAGER.getLoggerOverlay().info("UUID : " + uuid + " Name : " + ApiUtils.getNameFromUUID(uuid), false);
        Main.OVERLAY_MANAGER.getLoggerOverlay().info("Api URL : " + apiUrl, false);
        return getStringFromURL(apiUrl);
    }

    public static String getUUIDFromName(String playerName) {
        String apiUrl = "https://api.minecraftservices.com/minecraft/profile/lookup/name/" + playerName;

        try {
            URL url = new URI(apiUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                    JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                    String rawUUID = jsonObject.get("id").getAsString();
                    return formatUUIDWithHyphens(rawUUID);
                }
            }
        } catch (Exception e) {
            Main.LOGGER.error("ERROR CONNECTION TO MOJANG API CAN'T RETRIEVE THE UUID", e);
        }

        return null;
    }

    private static String formatUUIDWithHyphens(String rawUUID) {
        return rawUUID.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                "$1-$2-$3-$4-$5"
        );
    }

    public static String getNameFromUUID(String uuid) {
        try {
            uuid = uuid.replace("-", "");
            String urlString = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
            URL url = new URI(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();

            return jsonResponse.get("name").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            return "emptyname";
        }
    }
}
