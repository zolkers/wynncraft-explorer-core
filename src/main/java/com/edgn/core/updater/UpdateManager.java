package com.edgn.core.updater;

import com.edgn.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class UpdateManager {
    private static UpdateManager instance;

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(10)).build();
    private static final String BASE = "https://vps-14c83950.vps.ovh.net:8443";
    private static final String API = BASE + "/api/releases";

    private final Map<String, InstalledMod> installed = new HashMap<>();
    private final List<PendingUpdate> pendingUpdates = new ArrayList<>();
    private PublicKey serverKey;
    private volatile boolean startupChecked = false;

    public static UpdateManager getInstance() {
        if (instance == null) instance = new UpdateManager();
        return instance;
    }

    private UpdateManager() {
        // Nettoyer les vieux fichiers .old au démarrage
        cleanupOldFiles();
    }

    public void checkUpdatesOnStartup() {
        if (startupChecked) return;
        startupChecked = true;

        CompletableFuture.runAsync(() -> {
            try {
                if (fetchServerKey()) {
                    scanInstalled();
                    checkForUpdates();
                }
            } catch (Exception e) {
                Main.LOGGER.error("Startup update check failed: {}", e.getMessage());
            }
        });
    }

    private void checkForUpdates() {
        try {
            JsonObject req = buildCheckBody();
            HttpRequest rq = HttpRequest.newBuilder()
                    .uri(URI.create(API + "/check"))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "WynncraftExplorer/" + Main.VERSION)
                    .header("X-Client-Version", Main.VERSION)
                    .header("X-Request-Signature", CryptoUtils.signRequest(req.toString()))
                    .timeout(java.time.Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(req.toString()))
                    .build();

            HttpResponse<String> r = http.send(rq, HttpResponse.BodyHandlers.ofString());
            handleCheckResponse(r);
        } catch (Exception e) {
            Main.LOGGER.error("Check fail: {}", e.getMessage());
        }
    }

    private void handleCheckResponse(HttpResponse<String> r) {
        try {
            if (r.statusCode() != 200) return;

            String sig = r.headers().firstValue("X-Response-Signature").orElse("");
            if (sig.isEmpty() || serverKey == null || !CryptoUtils.verifySignature(r.body(), sig, serverKey)) {
                Main.LOGGER.warn("Invalid signature on update response");
                return;
            }

            JsonObject j = JsonParser.parseString(r.body()).getAsJsonObject();
            if (!j.has("updates")) return;

            for (var el : j.getAsJsonArray("updates")) {
                JsonObject u = el.getAsJsonObject();
                String modId = u.get("mod_id").getAsString();
                String currentVersion = u.get("current_version").getAsString();
                String newVersion = u.get("new_version").getAsString();

                if (!VersionComparator.isNewer(newVersion, currentVersion)) {
                    Main.LOGGER.debug("Skipping update for {} - {} is not newer than {}",
                            modId, newVersion, currentVersion);
                    continue;
                }

                PendingUpdate update = new PendingUpdate(
                        modId,
                        currentVersion,
                        newVersion,
                        u.get("download_url").getAsString(),
                        u.get("signature").getAsString(),
                        u.get("file_hash").getAsString(),
                        u.has("changelog") ? u.get("changelog").getAsString() : "",
                        u.has("critical") && u.get("critical").getAsBoolean()
                );

                // Télécharger l'update
                if (downloadUpdate(update)) {
                    pendingUpdates.add(update);
                    notifyUser(update);

                    // Ajouter le shutdown hook pour cette update
                    addShutdownHook(update);
                }
            }
        } catch (Exception e) {
            Main.LOGGER.error("Handle response fail: {}", e.getMessage());
        }
    }

    private boolean downloadUpdate(PendingUpdate update) {
        try {
            Main.LOGGER.info("Downloading update for {}: {} -> {}",
                    update.modId, update.currentVersion, update.newVersion);

            // Télécharger dans le dossier updates
            Path updateFile = getUpdateFilePath(update.modId);
            Files.createDirectories(updateFile.getParent());

            HttpRequest rq = HttpRequest.newBuilder()
                    .uri(URI.create(update.url))
                    .header("User-Agent", "WynncraftExplorer/" + Main.VERSION)
                    .timeout(java.time.Duration.ofMinutes(5))
                    .GET().build();

            HttpResponse<byte[]> r = http.send(rq, HttpResponse.BodyHandlers.ofByteArray());
            if (r.statusCode() != 200) {
                Main.LOGGER.error("Failed to download update: HTTP {}", r.statusCode());
                return false;
            }

            byte[] data = r.body();

            // Vérifier le hash
            String hash = CryptoUtils.calculateHashFromBytes(data);
            if (!update.hash.equals(hash)) {
                Main.LOGGER.error("Hash mismatch for update {}", update.modId);
                return false;
            }

            // Vérifier la signature
            if (!CryptoUtils.verifySignature(hash, update.signature, serverKey)) {
                Main.LOGGER.error("Invalid signature for update {}", update.modId);
                return false;
            }

            // Sauvegarder le fichier
            Files.write(updateFile, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            update.downloadedFile = updateFile;

            Main.LOGGER.info("Successfully downloaded update for {}", update.modId);
            return true;

        } catch (Exception e) {
            Main.LOGGER.error("Download failed for {}: {}", update.modId, e.getMessage());
            return false;
        }
    }

    /**
     * Ajoute un shutdown hook pour appliquer l'update
     * Stratégie Wynntils : copier le nouveau fichier par-dessus l'ancien
     */
    private void addShutdownHook(PendingUpdate update) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (update.downloadedFile == null || !Files.exists(update.downloadedFile)) {
                    Main.LOGGER.warn("Update file not found for {}", update.modId);
                    return;
                }

                Path targetFile = getTargetPath(update.modId);

                if (!Files.exists(targetFile)) {
                    Main.LOGGER.warn("Target file not found: {}", targetFile);
                    return;
                }

                Main.LOGGER.info("Applying update for {} at shutdown", update.modId);

                // Créer un backup avec .old
                Path backupFile = targetFile.resolveSibling(targetFile.getFileName() + ".old");
                try {
                    Files.copy(targetFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    Main.LOGGER.warn("Could not create backup: {}", e.getMessage());
                }

                // STRATÉGIE WYNNTILS : Copier le nouveau fichier par-dessus l'ancien
                // Au lieu de move/rename, on utilise une copie qui écrase
                copyFileOverwrite(update.downloadedFile, targetFile);

                // Supprimer le fichier téléchargé
                try {
                    Files.delete(update.downloadedFile);
                } catch (Exception e) {
                    Main.LOGGER.warn("Could not delete update file: {}", e.getMessage());
                }

                Main.LOGGER.info("Successfully applied update for {}!", update.modId);

            } catch (Exception e) {
                Main.LOGGER.error("Failed to apply update for {}: {}", update.modId, e.getMessage());
            }
        }, "WE-Update-" + update.modId));
    }

    /**
     * Copie un fichier en écrasant la destination
     * Utilise une approche similaire à Wynntils
     */
    private void copyFileOverwrite(Path source, Path destination) throws IOException {
        // Méthode 1 : Utiliser Files.copy avec REPLACE_EXISTING
        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            return;
        } catch (Exception e) {
            Main.LOGGER.debug("Files.copy failed, trying stream copy: {}", e.getMessage());
        }

        // Méthode 2 : Copie via streams (comme Wynntils)
        try (InputStream in = Files.newInputStream(source);
             OutputStream out = Files.newOutputStream(destination,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }

    private Path getUpdateFilePath(String modId) {
        MinecraftClient mc = MinecraftClient.getInstance();
        File runDir = mc != null ? mc.runDirectory : new File(".");
        return runDir.toPath()
                .resolve("config")
                .resolve("wynncraft-explorer")
                .resolve("updates")
                .resolve(modId + "-update.jar");
    }

    private Path getTargetPath(String modId) {
        if ("wynncraft-explorer".equals(modId)) {
            return CryptoUtils.getCurrentJarPath();
        }

        InstalledMod im = installed.get(modId);
        if (im != null && im.path != null && Files.exists(im.path)) {
            return im.path;
        }

        return Paths.get("mods", modId + ".jar");
    }

    private void cleanupOldFiles() {
        try {
            Path modsDir = Paths.get("mods");
            if (!Files.exists(modsDir)) return;

            // Supprimer tous les .old
            Files.walk(modsDir, 1)
                    .filter(p -> p.toString().endsWith(".old"))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                            Main.LOGGER.debug("Cleaned up old file: {}", p.getFileName());
                        } catch (IOException e) {
                            Main.LOGGER.warn("Could not delete old file: {}", p);
                        }
                    });

            // Nettoyer le dossier updates aussi
            Path updatesDir = Paths.get("config/wynncraft-explorer/updates");
            if (Files.exists(updatesDir)) {
                Files.walk(updatesDir, 1)
                        .filter(Files::isRegularFile)
                        .forEach(p -> {
                            try {
                                // Supprimer les fichiers de plus de 7 jours
                                if (Files.getLastModifiedTime(p).toMillis() <
                                        System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) {
                                    Files.deleteIfExists(p);
                                }
                            } catch (IOException e) {
                                // Ignorer
                            }
                        });
            }

        } catch (Exception e) {
            Main.LOGGER.warn("Cleanup failed: {}", e.getMessage());
        }
    }

    private void notifyUser(PendingUpdate update) {
        try {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc != null && mc.player != null) {
                String tag = update.critical ? "§c[CRITICAL UPDATE]" : "§a[UPDATE]";
                String msg = String.format("§7[§6WE§7] %s §f%s §7v%s → §av%s §e(will apply on restart)",
                        tag, update.modId, update.currentVersion, update.newVersion);
                mc.execute(() -> mc.player.sendMessage(Text.literal(msg), false));

                if (!update.changelog.isEmpty()) {
                    mc.execute(() -> mc.player.sendMessage(Text.literal("§7Changes: §f" + update.changelog), false));
                }
            }
        } catch (Exception ignored) {}
    }

    // Méthodes inchangées...

    private void scanInstalled() {
        installed.clear();
        String coreVer = Main.VERSION;
        Path coreJar = CryptoUtils.getCurrentJarPath();
        String coreHash = CryptoUtils.calculateSecureFileHash(coreJar);
        installed.put("wynncraft-explorer", new InstalledMod("wynncraft-explorer", coreVer, "core", coreHash, coreJar));

        try {
            Path mods = Paths.get("mods");
            if (Files.exists(mods)) {
                Files.list(mods)
                        .filter(this::isValidJar)
                        .forEach(this::scanOne);
            }
        } catch (Exception e) {
            Main.LOGGER.warn("Scan mods fail: {}", e.getMessage());
        }
    }

    private boolean isValidJar(Path p) {
        String n = p.getFileName().toString();
        if (!n.endsWith(".jar")) return false;
        if (n.endsWith(".old")) return false;
        if (n.contains("-sources") || n.contains("-dev") || n.contains("-javadoc")) return false;
        try {
            if (p.equals(CryptoUtils.getCurrentJarPath())) return false;
        } catch (Exception ignored) {}
        return true;
    }

    private void scanOne(Path jar) {
        try (JarFile jf = new JarFile(jar.toFile())) {
            Manifest mf = jf.getManifest();
            if (mf == null) return;
            var a = mf.getMainAttributes();
            String modId = Optional.ofNullable(a.getValue("Mod-Id"))
                    .orElse(Optional.ofNullable(a.getValue("Implementation-Title"))
                            .orElse(Optional.ofNullable(a.getValue("Bundle-SymbolicName"))
                                    .orElse(null)));
            String ver = Optional.ofNullable(a.getValue("Mod-Version"))
                    .orElse(Optional.ofNullable(a.getValue("Implementation-Version"))
                            .orElse(Optional.ofNullable(a.getValue("Bundle-Version")).orElse(null)));
            if (modId != null && ver != null) {
                String h = CryptoUtils.calculateSecureFileHash(jar);
                installed.put(modId, new InstalledMod(modId, ver, "extension", h, jar));
            }
        } catch (Exception ignored) {}
    }

    private boolean fetchServerKey() {
        try {
            HttpRequest rq = HttpRequest.newBuilder()
                    .uri(URI.create(API + "/public-key"))
                    .header("User-Agent", "WynncraftExplorer/" + Main.VERSION)
                    .timeout(java.time.Duration.ofSeconds(15))
                    .GET().build();
            HttpResponse<String> r = http.send(rq, HttpResponse.BodyHandlers.ofString());
            if (r.statusCode() != 200) return false;

            JsonObject j = JsonParser.parseString(r.body()).getAsJsonObject();
            if (!j.has("public_key") || !j.has("test_message") || !j.has("test_signature")) return false;

            PublicKey pk = CryptoUtils.parsePublicKeyFromPem(j.get("public_key").getAsString());
            if (pk == null) return false;

            String msg = j.get("test_message").getAsString();
            String sig = j.get("test_signature").getAsString();
            if (!CryptoUtils.verifySignature(msg, sig, pk)) return false;

            serverKey = pk;
            return true;
        } catch (Exception e) {
            Main.LOGGER.error("Fetch key fail: {}", e.getMessage());
            return false;
        }
    }

    private JsonObject buildCheckBody() {
        JsonObject req = new JsonObject();
        req.addProperty("action", "check_updates");
        req.addProperty("client_id", CryptoUtils.generateClientId());
        req.addProperty("timestamp", System.currentTimeMillis());
        JsonArray arr = new JsonArray();
        for (InstalledMod m : installed.values()) {
            JsonObject o = new JsonObject();
            o.addProperty("mod_id", m.id);
            o.addProperty("version", m.version);
            o.addProperty("type", m.type);
            o.addProperty("hash", m.hash);
            arr.add(o);
        }
        req.add("installed_mods", arr);
        return req;
    }

    public record InstalledMod(String id, String version, String type, String hash, Path path) {}

    public static final class PendingUpdate {
        public final String modId, currentVersion, newVersion, url, signature, hash, changelog;
        public final boolean critical;
        public Path downloadedFile;

        public PendingUpdate(String modId, String currentVersion, String newVersion, String url,
                             String signature, String hash, String changelog, boolean critical) {
            this.modId = modId;
            this.currentVersion = currentVersion;
            this.newVersion = newVersion;
            this.url = url;
            this.signature = signature;
            this.hash = hash;
            this.changelog = changelog;
            this.critical = critical;
        }
    }
}