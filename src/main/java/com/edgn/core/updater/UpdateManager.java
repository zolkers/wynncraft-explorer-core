package com.edgn.core.updater;

import com.edgn.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class UpdateManager {
    private static UpdateManager instance;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();

    private final String UPDATE_SERVER_URL = "https://vps-14c83950.vps.ovh.net:8443";
    private final String API_ENDPOINT = UPDATE_SERVER_URL + "/api/releases";

    private final Map<String, InstalledMod> installedMods = new HashMap<>();
    private final List<PendingUpdate> pendingUpdates = new ArrayList<>();
    private PublicKey serverPublicKey;
    private volatile boolean updatesChecked = false;

    private UpdateManager() {
        registerShutdownHook();
    }

    public static UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Main.LOGGER.info("[Updater] JVM shutdown - checking for updates...");

                if (fetchServerKey()) {
                    scanInstalledMods();
                    checkForUpdates();
                    Thread.sleep(3000);
                }

                if (!pendingUpdates.isEmpty()) {
                    Main.LOGGER.info("[Updater] Installing {} updates...", pendingUpdates.size());
                    createAndExecuteUpdateScript();
                }

            } catch (Exception e) {
                Main.LOGGER.error("[Updater] Shutdown hook failed: {}", e.getMessage());
            }
        }, "WE-Update-Hook"));
    }

    public void checkUpdatesOnStartup() {
        if (updatesChecked) return;
        updatesChecked = true;

        Main.LOGGER.info("[Updater] Checking for updates on startup...");

        CompletableFuture.runAsync(() -> {
            try {
                if (fetchServerKey()) {
                    scanInstalledMods();
                    checkForUpdates();
                }
            } catch (Exception e) {
                Main.LOGGER.error("[Updater] Startup check failed: {}", e.getMessage());
            }
        });
    }

    private void scanInstalledMods() {
        installedMods.clear();

        // 1. CORE MOD - Utiliser la version hardcodée depuis Main.VERSION
        String coreVersion = Main.VERSION;
        String coreHash = CryptoUtils.calculateSecureFileHash(getCurrentJarPath());

        installedMods.put("wynncraft-explorer", new InstalledMod(
                "wynncraft-explorer",
                coreVersion,
                "core",
                coreHash,
                getCurrentJarPath()
        ));

        Main.LOGGER.info("[Updater] Found CORE MOD: wynncraft-explorer v{}", coreVersion);

        // 2. EXTENSIONS - Scanner le dossier mods
        try {
            Path modsPath = Paths.get("mods");
            if (!Files.exists(modsPath)) return;

            Files.list(modsPath)
                    .filter(this::isValidModJar)
                    .forEach(this::scanExtensionJar);

        } catch (Exception e) {
            Main.LOGGER.error("[Updater] Error scanning mods directory: {}", e.getMessage());
        }
    }

    private boolean isValidModJar(Path jarPath) {
        String fileName = jarPath.getFileName().toString();

        // Filtrer les JAR sources et autres fichiers non-mod
        if (!fileName.endsWith(".jar")) return false;
        if (fileName.contains("-sources")) return false;
        if (fileName.contains("-dev")) return false;
        if (fileName.contains("-javadoc")) return false;

        // Ne pas scanner le core mod lui-même
        try {
            Path currentJar = getCurrentJarPath();
            if (jarPath.equals(currentJar)) return false;
        } catch (Exception e) {
            // Ignore
        }

        return true;
    }

    private void scanExtensionJar(Path jarPath) {
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                Main.LOGGER.debug("[Updater] No manifest in {}, skipping", jarPath.getFileName());
                return;
            }

            var attributes = manifest.getMainAttributes();

            // Chercher les métadonnées de l'extension
            String modId = getModIdFromManifest(attributes);
            String version = getVersionFromManifest(attributes, jarPath);

            if (modId != null && version != null) {
                String hash = CryptoUtils.calculateSecureFileHash(jarPath);
                installedMods.put(modId, new InstalledMod(modId, version, "extension", hash, jarPath));
                Main.LOGGER.info("[Updater] Found EXTENSION: {} v{}", modId, version);
            } else {
                Main.LOGGER.debug("[Updater] Could not extract mod info from {}", jarPath.getFileName());
            }

        } catch (Exception e) {
            Main.LOGGER.warn("[Updater] Failed to scan {}: {}", jarPath.getFileName(), e.getMessage());
        }
    }

    private String getModIdFromManifest(java.util.jar.Attributes attributes) {
        // Plusieurs façons possibles de détecter l'ID du mod
        String modId = attributes.getValue("Mod-Id");
        if (modId != null) return modId;

        modId = attributes.getValue("Implementation-Title");
        if (modId != null) return modId;

        modId = attributes.getValue("Bundle-SymbolicName");
        if (modId != null) return modId;

        // Si c'est une extension connue, détecter par classe
        String mainClass = attributes.getValue("Main-Class");
        if (mainClass != null && mainClass.contains("dernext")) {
            return "wynncraft-explorer-dern";
        }

        return null;
    }

    private String getVersionFromManifest(java.util.jar.Attributes attributes, Path jarPath) {
        // 1. Version explicite dans le manifest
        String version = attributes.getValue("Mod-Version");
        if (version != null && !version.isEmpty()) return version;

        version = attributes.getValue("Implementation-Version");
        if (version != null && !version.isEmpty()) return version;

        version = attributes.getValue("Bundle-Version");
        if (version != null && !version.isEmpty()) return version;

        // 2. Extraire depuis le nom du fichier
        // Format attendu: modname-version.jar
        String fileName = jarPath.getFileName().toString();
        if (fileName.endsWith(".jar")) {
            String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf(".jar"));

            // Chercher le dernier pattern -version
            String[] parts = nameWithoutExt.split("-");
            if (parts.length >= 2) {
                String lastPart = parts[parts.length - 1];
                // Vérifier si ça ressemble à une version (contient des chiffres)
                if (lastPart.matches(".*\\d.*")) {
                    return lastPart;
                }
            }
        }

        return null;
    }

    private boolean fetchServerKey() {
        try {
            Main.LOGGER.info("[Updater] Fetching server public key...");

            HttpRequest keyRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_ENDPOINT + "/public-key"))
                    .header("User-Agent", "WynncraftExplorer/" + Main.VERSION)
                    .timeout(java.time.Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(keyRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                Main.LOGGER.error("[Updater] Failed to fetch server key: HTTP {}", response.statusCode());
                return false;
            }

            JsonObject keyData = JsonParser.parseString(response.body()).getAsJsonObject();
            String publicKeyPem = keyData.get("public_key").getAsString();
            serverPublicKey = CryptoUtils.parsePublicKeyFromPem(publicKeyPem);

            if (serverPublicKey == null) {
                Main.LOGGER.error("[Updater] Failed to parse server public key");
                return false;
            }

            // Test de vérification
            String testMessage = keyData.get("test_message").getAsString();
            String testSignature = keyData.get("test_signature").getAsString();

            if (!CryptoUtils.verifySignature(testMessage, testSignature, serverPublicKey)) {
                Main.LOGGER.error("[Updater] Server key verification failed");
                return false;
            }

            Main.LOGGER.info("[Updater] Server key verified successfully");
            return true;

        } catch (Exception e) {
            Main.LOGGER.error("[Updater] Error fetching server key: {}", e.getMessage());
            return false;
        }
    }

    private void checkForUpdates() {
        try {
            JsonObject request = createUpdateRequest();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_ENDPOINT + "/check"))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "WynncraftExplorer/" + Main.VERSION)
                    .header("X-Client-Version", Main.VERSION)
                    .header("X-Request-Signature", CryptoUtils.signRequest(request.toString()))
                    .timeout(java.time.Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(request.toString()))
                    .build();

            HttpResponse<String> response = httpClient.sendAsync(
                            httpRequest, HttpResponse.BodyHandlers.ofString())
                    .get(30, java.util.concurrent.TimeUnit.SECONDS);

            handleUpdateResponse(response);

        } catch (Exception e) {
            Main.LOGGER.error("[Updater] Update check failed: {}", e.getMessage());
        }
    }

    private JsonObject createUpdateRequest() {
        JsonObject request = new JsonObject();
        request.addProperty("action", "check_updates");
        request.addProperty("client_id", CryptoUtils.generateClientId());
        request.addProperty("timestamp", System.currentTimeMillis());

        JsonArray modsArray = new JsonArray();
        for (InstalledMod mod : installedMods.values()) {
            JsonObject modObj = new JsonObject();
            modObj.addProperty("mod_id", mod.id);
            modObj.addProperty("version", mod.version);
            modObj.addProperty("type", mod.type);
            modObj.addProperty("hash", mod.hash);
            modsArray.add(modObj);
        }
        request.add("installed_mods", modsArray);

        return request;
    }

    private void handleUpdateResponse(HttpResponse<String> response) {
        try {
            if (response.statusCode() != 200) {
                Main.LOGGER.warn("[Updater] Server returned status: {}", response.statusCode());
                return;
            }

            // Vérifier signature de la réponse
            String signature = response.headers().firstValue("X-Response-Signature").orElse("");
            if (!CryptoUtils.verifySignature(response.body(), signature, serverPublicKey)) {
                Main.LOGGER.error("[Updater] Response signature verification failed!");
                return;
            }

            JsonObject updateInfo = JsonParser.parseString(response.body()).getAsJsonObject();

            if (updateInfo.has("updates") && updateInfo.getAsJsonArray("updates").size() > 0) {
                processAvailableUpdates(updateInfo.getAsJsonArray("updates"));
            } else {
                Main.LOGGER.info("[Updater] No updates available");
            }

        } catch (Exception e) {
            Main.LOGGER.error("[Updater] Error handling update response: {}", e.getMessage());
        }
    }

    private void processAvailableUpdates(JsonArray updates) {
        for (var updateElement : updates) {
            JsonObject updateObj = updateElement.getAsJsonObject();

            String modId = updateObj.get("mod_id").getAsString();
            String currentVersion = updateObj.get("current_version").getAsString();
            String newVersion = updateObj.get("new_version").getAsString();
            String downloadUrl = updateObj.get("download_url").getAsString();
            String signature = updateObj.get("signature").getAsString();
            String fileHash = updateObj.get("file_hash").getAsString();
            String changelog = updateObj.has("changelog") ? updateObj.get("changelog").getAsString() : "";
            boolean critical = updateObj.has("critical") && updateObj.get("critical").getAsBoolean();

            // Vérifier que nous avons vraiment ce mod installé
            if (!installedMods.containsKey(modId)) {
                Main.LOGGER.debug("[Updater] Ignoring update for unknown mod: {}", modId);
                continue;
            }

            // Vérifier que les versions correspondent
            InstalledMod installedMod = installedMods.get(modId);
            if (!installedMod.version.equals(currentVersion)) {
                Main.LOGGER.warn("[Updater] Version mismatch for {}: server says {} but we have {}",
                        modId, currentVersion, installedMod.version);
                continue;
            }

            PendingUpdate pendingUpdate = new PendingUpdate(
                    modId, currentVersion, newVersion, downloadUrl,
                    signature, fileHash, changelog, critical
            );

            if (downloadAndVerifyUpdate(pendingUpdate)) {
                pendingUpdates.add(pendingUpdate);

                String priority = critical ? "CRITICAL" : "NORMAL";
                Main.LOGGER.info("[Updater] {} update ready: {} v{} -> v{}",
                        priority, modId, currentVersion, newVersion);

                notifyUser(pendingUpdate);
            }
        }
    }

    private boolean downloadAndVerifyUpdate(PendingUpdate update) {
        try {
            Main.LOGGER.info("[Updater] Downloading update for {}...", update.modId);

            HttpRequest downloadRequest = HttpRequest.newBuilder()
                    .uri(URI.create(update.downloadUrl))
                    .header("User-Agent", "WynncraftExplorer/" + Main.VERSION)
                    .timeout(java.time.Duration.ofMinutes(5))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(
                    downloadRequest, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                Main.LOGGER.error("[Updater] Download failed for {}: HTTP {}",
                        update.modId, response.statusCode());
                return false;
            }

            byte[] fileData = response.body();

            // Vérifier le hash
            String actualHash = CryptoUtils.calculateHashFromBytes(fileData);
            if (!update.fileHash.equals(actualHash)) {
                Main.LOGGER.error("[Updater] Hash mismatch for {}: expected {} got {}",
                        update.modId, update.fileHash, actualHash);
                return false;
            }

            // Vérifier la signature
            if (!CryptoUtils.verifySignature(actualHash, update.signature, serverPublicKey)) {
                Main.LOGGER.error("[Updater] Signature verification failed for {}", update.modId);
                return false;
            }

            // Sauvegarder temporairement
            update.tempFilePath = CryptoUtils.saveTemporaryFile(update.modId, fileData);
            if (update.tempFilePath == null) {
                return false;
            }

            Main.LOGGER.info("[Updater] Successfully verified update for {}", update.modId);
            return true;

        } catch (Exception e) {
            Main.LOGGER.error("[Updater] Error downloading/verifying {}: {}",
                    update.modId, e.getMessage());
            return false;
        }
    }


    private void createAndExecuteUpdateScript() {
        try {
            UpdateScriptGenerator.createUpdateScript(pendingUpdates, installedMods);
            UpdateScriptGenerator.executeUpdateScript();
        } catch (Exception e) {
            Main.LOGGER.error("[Updater] Failed to create/execute update script: {}", e.getMessage());
        }
    }

    private void notifyUser(PendingUpdate update) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.player != null) {
                mc.execute(() -> {
                    String priority = update.critical ? "§c[CRITICAL UPDATE]" : "§a[UPDATE AVAILABLE]";
                    String message = String.format("§7[§6WE§7] %s §f%s §7v%s → §av%s",
                            priority, update.modId, update.currentVersion, update.newVersion);

                    mc.player.sendMessage(Text.literal(message), false);

                    if (!update.changelog.isEmpty()) {
                        mc.player.sendMessage(Text.literal("§7[§6WE§7] §7Changes: §f" + update.changelog), false);
                    }

                    mc.player.sendMessage(Text.literal("§7[§6WE§7] §eUpdate will be installed on restart"), false);
                });
            }

            // Notifier l'extension si elle est présente
            try {
                Class<?> notifClass = Class.forName("com.edgn.dernext.updater.UpdateNotificationInterface");
                Object instance = notifClass.getMethod("getInstance").invoke(null);
                notifClass.getMethod("notifyUpdateAvailable", String.class, String.class, String.class, boolean.class, String.class)
                        .invoke(instance, update.modId, update.currentVersion, update.newVersion, update.critical, update.changelog);
            } catch (Exception e) {
                Main.LOGGER.debug("[Updater] Extension notification skipped: {}", e.getMessage());
            }

        } catch (Exception e) {
            Main.LOGGER.error("[Updater] Failed to notify user: {}", e.getMessage());
        }
    }

    private Path getCurrentJarPath() {
        return CryptoUtils.getCurrentJarPath();
    }

    public static class InstalledMod {
        final String id, version, type, hash;
        final Path filePath;

        InstalledMod(String id, String version, String type, String hash, Path filePath) {
            this.id = id; this.version = version; this.type = type;
            this.hash = hash; this.filePath = filePath;
        }
    }


    public static class PendingUpdate {
        final String modId, currentVersion, newVersion, downloadUrl;
        final String signature, fileHash, changelog;
        final boolean critical;
        Path tempFilePath;

        PendingUpdate(String modId, String currentVersion, String newVersion,
                      String downloadUrl, String signature, String fileHash,
                      String changelog, boolean critical) {
            this.modId = modId; this.currentVersion = currentVersion;
            this.newVersion = newVersion; this.downloadUrl = downloadUrl;
            this.signature = signature; this.fileHash = fileHash;
            this.changelog = changelog; this.critical = critical;
        }
    }


}