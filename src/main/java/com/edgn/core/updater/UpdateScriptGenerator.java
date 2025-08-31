package com.edgn.core.updater;

import com.edgn.Main;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public final class UpdateScriptGenerator {

    public static void createUpdateScript(List<UpdateManager.PendingUpdate> updates,
                                          Map<String, UpdateManager.InstalledMod> installedMods) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            createWindowsScript(updates, installedMods);
        } else {
            createUnixScript(updates, installedMods);
        }
    }

    private static void createWindowsScript(List<UpdateManager.PendingUpdate> updates,
                                            Map<String, UpdateManager.InstalledMod> installedMods) throws IOException {
        Path scriptPath = Paths.get("we_update.bat");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptPath.toFile()))) {
            writer.write("@echo off\n");
            writer.write("title Wynncraft Explorer - Auto Updater\n");
            writer.write("color 0A\n");
            writer.write("echo.\n");
            writer.write("echo  ===============================================\n");
            writer.write("echo  ^|        Wynncraft Explorer Updater        ^|\n");
            writer.write("echo  ===============================================\n");
            writer.write("echo.\n");
            writer.write("echo [INFO] Starting update process...\n");
            writer.write("echo [INFO] Found " + updates.size() + " update(s) to install\n");
            writer.write("echo.\n");
            writer.write("timeout /t 3 /nobreak >nul\n\n");

            writer.write("set UPDATE_COUNT=0\n");
            writer.write("set SUCCESS_COUNT=0\n");
            writer.write("set ERROR_COUNT=0\n\n");

            for (UpdateManager.PendingUpdate update : updates) {
                Path targetPath = getTargetPath(update, installedMods);
                Path tempPath = update.tempFilePath;

                writer.write("set /a UPDATE_COUNT+=1\n");
                writer.write(String.format("echo [UPDATE %%UPDATE_COUNT%%/%d] %s v%s ^-^> v%s\n",
                        updates.size(), update.modId, update.currentVersion, update.newVersion));

                if (update.critical) {
                    writer.write("echo [WARNING] This is a CRITICAL update!\n");
                }

                writer.write(String.format("if not exist \"%s\" (\n", tempPath));
                writer.write(String.format("    echo [ERROR] Temporary file not found: %s\n", update.modId));
                writer.write("    set /a ERROR_COUNT+=1\n");
                writer.write("    goto :next_update\n");
                writer.write(")\n\n");

                writer.write(String.format("if exist \"%s\" (\n", targetPath));
                writer.write(String.format("    echo [INFO] Creating backup of %s...\n", update.modId));
                writer.write(String.format("    copy \"%s\" \"%s.backup\" >nul 2>&1\n", targetPath, targetPath));
                writer.write(String.format("    if errorlevel 1 (\n"));
                writer.write(String.format("        echo [ERROR] Failed to create backup for %s\n", update.modId));
                writer.write("        set /a ERROR_COUNT+=1\n");
                writer.write("        goto :next_update\n");
                writer.write(String.format("    )\n"));
                writer.write("    echo [OK] Backup created\n");
                writer.write(") else (\n");
                writer.write(String.format("    echo [INFO] No existing file to backup for %s\n", update.modId));
                writer.write(")\n\n");

                writer.write(String.format("mkdir \"%s\" 2>nul\n", targetPath.getParent()));

                writer.write(String.format("echo [INFO] Installing %s v%s...\n", update.modId, update.newVersion));
                writer.write(String.format("copy \"%s\" \"%s\" >nul 2>&1\n", tempPath, targetPath));
                writer.write("if errorlevel 1 (\n");
                writer.write(String.format("    echo [ERROR] Failed to install %s\n", update.modId));

                writer.write(String.format("    if exist \"%s.backup\" (\n", targetPath));
                writer.write("        echo [INFO] Attempting to restore backup...\n");
                writer.write(String.format("        copy \"%s.backup\" \"%s\" >nul 2>&1\n", targetPath, targetPath));
                writer.write("        if errorlevel 1 (\n");
                writer.write(String.format("            echo [ERROR] Failed to restore backup for %s\n", update.modId));
                writer.write("        ) else (\n");
                writer.write(String.format("            echo [OK] Backup restored for %s\n", update.modId));
                writer.write("        )\n");
                writer.write(String.format("    )\n"));
                writer.write("    set /a ERROR_COUNT+=1\n");
                writer.write("    goto :next_update\n");
                writer.write(")\n\n");

                writer.write(String.format("if not exist \"%s\" (\n", targetPath));
                writer.write(String.format("    echo [ERROR] Installation verification failed for %s\n", update.modId));
                writer.write("    set /a ERROR_COUNT+=1\n");
                writer.write("    goto :next_update\n");
                writer.write(")\n\n");

                writer.write(String.format("del \"%s\" >nul 2>&1\n", tempPath));
                writer.write(String.format("echo [SUCCESS] %s updated to v%s\n", update.modId, update.newVersion));
                writer.write("set /a SUCCESS_COUNT+=1\n");

                writer.write(":next_update\n");
                writer.write("echo.\n\n");
            }

            writer.write("echo ===============================================\n");
            writer.write("echo                UPDATE SUMMARY\n");
            writer.write("echo ===============================================\n");
            writer.write("echo Total updates processed: %%UPDATE_COUNT%%\n");
            writer.write("echo Successful: %%SUCCESS_COUNT%%\n");
            writer.write("echo Failed: %%ERROR_COUNT%%\n");
            writer.write("echo.\n\n");

            writer.write("if %%ERROR_COUNT%% equ 0 (\n");
            writer.write("    echo [INFO] Cleaning up backup files...\n");
            for (UpdateManager.PendingUpdate update : updates) {
                Path targetPath = getTargetPath(update, installedMods);
                writer.write(String.format("    del \"%s.backup\" >nul 2>&1\n", targetPath));
            }
            writer.write("    echo [OK] Backup files removed\n");
            writer.write(") else (\n");
            writer.write("    echo [WARNING] Some updates failed - backup files preserved\n");
            writer.write("    echo [INFO] You can manually restore backups if needed\n");
            writer.write(")\n\n");

            writer.write("if %%ERROR_COUNT%% equ 0 (\n");
            writer.write("    echo [SUCCESS] All updates completed successfully!\n");
            writer.write("    echo [INFO] Your Wynncraft Explorer is now up to date.\n");
            writer.write("    color 0B\n");
            writer.write(") else (\n");
            writer.write("    echo [WARNING] Some updates failed. Check the log above.\n");
            writer.write("    color 0C\n");
            writer.write(")\n\n");

            writer.write("echo.\n");
            writer.write("echo You can now restart Minecraft to use the updated mods.\n");
            writer.write("echo This window will close automatically in 10 seconds...\n");
            writer.write("echo (or press any key to close immediately)\n");
            writer.write("timeout /t 10 >nul\n\n");

            writer.write("echo [INFO] Cleaning up updater...\n");
            writer.write("del \"%~f0\" >nul 2>&1\n");
        }

        scriptPath.toFile().setExecutable(true);
        Main.LOGGER.info("[Updater] Created Windows update script: {}", scriptPath);
    }

    private static void createUnixScript(List<UpdateManager.PendingUpdate> updates,
                                         Map<String, UpdateManager.InstalledMod> installedMods) throws IOException {
        Path scriptPath = Paths.get("we_update.sh");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scriptPath.toFile()))) {
            writer.write("#!/bin/bash\n\n");

            writer.write("RED='\\033[0;31m'\n");
            writer.write("GREEN='\\033[0;32m'\n");
            writer.write("YELLOW='\\033[1;33m'\n");
            writer.write("BLUE='\\033[0;34m'\n");
            writer.write("NC='\\033[0m'\n\n");

            writer.write("clear\n");
            writer.write("echo -e \"${BLUE}===============================================${NC}\"\n");
            writer.write("echo -e \"${BLUE}|        Wynncraft Explorer Updater        |${NC}\"\n");
            writer.write("echo -e \"${BLUE}===============================================${NC}\"\n");
            writer.write("echo\n");
            writer.write("echo -e \"${GREEN}[INFO]${NC} Starting update process...\"\n");
            writer.write("echo -e \"${GREEN}[INFO]${NC} Found " + updates.size() + " update(s) to install\"\n");
            writer.write("echo\n");
            writer.write("sleep 3\n\n");

            writer.write("UPDATE_COUNT=0\n");
            writer.write("SUCCESS_COUNT=0\n");
            writer.write("ERROR_COUNT=0\n\n");

            writer.write("handle_error() {\n");
            writer.write("    echo -e \"${RED}[ERROR]${NC} $1\"\n");
            writer.write("    ERROR_COUNT=$((ERROR_COUNT + 1))\n");
            writer.write("    return 1\n");
            writer.write("}\n\n");

            writer.write("handle_success() {\n");
            writer.write("    echo -e \"${GREEN}[SUCCESS]${NC} $1\"\n");
            writer.write("    SUCCESS_COUNT=$((SUCCESS_COUNT + 1))\n");
            writer.write("}\n\n");

            writer.write("handle_info() {\n");
            writer.write("    echo -e \"${BLUE}[INFO]${NC} $1\"\n");
            writer.write("}\n\n");

            writer.write("handle_warning() {\n");
            writer.write("    echo -e \"${YELLOW}[WARNING]${NC} $1\"\n");
            writer.write("}\n\n");

            for (UpdateManager.PendingUpdate update : updates) {
                Path targetPath = getTargetPath(update, installedMods);
                Path tempPath = update.tempFilePath;

                writer.write("UPDATE_COUNT=$((UPDATE_COUNT + 1))\n");
                writer.write(String.format("echo -e \"${BLUE}[UPDATE $UPDATE_COUNT/%d]${NC} %s v%s -> v%s\"\n",
                        updates.size(), update.modId, update.currentVersion, update.newVersion));

                if (update.critical) {
                    writer.write("handle_warning \"This is a CRITICAL update!\"\n");
                }

                writer.write(String.format("if [ ! -f \"%s\" ]; then\n", tempPath));
                writer.write(String.format("    handle_error \"Temporary file not found: %s\"\n", update.modId));
                writer.write("    continue\n");
                writer.write("fi\n\n");

                writer.write(String.format("if [ -f \"%s\" ]; then\n", targetPath));
                writer.write(String.format("    handle_info \"Creating backup of %s...\"\n", update.modId));
                writer.write(String.format("    if cp \"%s\" \"%s.backup\" 2>/dev/null; then\n", targetPath, targetPath));
                writer.write("        handle_info \"Backup created\"\n");
                writer.write("    else\n");
                writer.write(String.format("        handle_error \"Failed to create backup for %s\"\n", update.modId));
                writer.write("        continue\n");
                writer.write("    fi\n");
                writer.write("else\n");
                writer.write(String.format("    handle_info \"No existing file to backup for %s\"\n", update.modId));
                writer.write("fi\n\n");

                writer.write(String.format("mkdir -p \"%s\" 2>/dev/null\n", targetPath.getParent()));

                writer.write(String.format("handle_info \"Installing %s v%s...\"\n", update.modId, update.newVersion));
                writer.write(String.format("if cp \"%s\" \"%s\" 2>/dev/null; then\n", tempPath, targetPath));

                writer.write(String.format("    if [ -f \"%s\" ]; then\n", targetPath));
                writer.write(String.format("        handle_success \"%s updated to v%s\"\n", update.modId, update.newVersion));
                writer.write(String.format("        rm -f \"%s\" 2>/dev/null\n", tempPath));
                writer.write("    else\n");
                writer.write(String.format("        handle_error \"Installation verification failed for %s\"\n", update.modId));
                writer.write("    fi\n");
                writer.write("else\n");
                writer.write(String.format("    handle_error \"Failed to install %s\"\n", update.modId));

                writer.write(String.format("    if [ -f \"%s.backup\" ]; then\n", targetPath));
                writer.write("        handle_info \"Attempting to restore backup...\"\n");
                writer.write(String.format("        if cp \"%s.backup\" \"%s\" 2>/dev/null; then\n", targetPath, targetPath));
                writer.write(String.format("            handle_info \"Backup restored for %s\"\n", update.modId));
                writer.write("        else\n");
                writer.write(String.format("            handle_error \"Failed to restore backup for %s\"\n", update.modId));
                writer.write("        fi\n");
                writer.write("    fi\n");
                writer.write("fi\n\n");

                writer.write("echo\n");
            }

            writer.write("echo -e \"${BLUE}===============================================${NC}\"\n");
            writer.write("echo -e \"${BLUE}                UPDATE SUMMARY${NC}\"\n");
            writer.write("echo -e \"${BLUE}===============================================${NC}\"\n");
            writer.write("echo \"Total updates processed: $UPDATE_COUNT\"\n");
            writer.write("echo -e \"${GREEN}Successful: $SUCCESS_COUNT${NC}\"\n");
            writer.write("echo -e \"${RED}Failed: $ERROR_COUNT${NC}\"\n");
            writer.write("echo\n\n");

            writer.write("if [ $ERROR_COUNT -eq 0 ]; then\n");
            writer.write("    handle_info \"Cleaning up backup files...\"\n");
            for (UpdateManager.PendingUpdate update : updates) {
                Path targetPath = getTargetPath(update, installedMods);
                writer.write(String.format("    rm -f \"%s.backup\" 2>/dev/null\n", targetPath));
            }
            writer.write("    handle_info \"Backup files removed\"\n");
            writer.write("else\n");
            writer.write("    handle_warning \"Some updates failed - backup files preserved\"\n");
            writer.write("    handle_info \"You can manually restore backups if needed\"\n");
            writer.write("fi\n\n");

            writer.write("if [ $ERROR_COUNT -eq 0 ]; then\n");
            writer.write("    echo -e \"${GREEN}[SUCCESS]${NC} All updates completed successfully!\"\n");
            writer.write("    echo -e \"${GREEN}[INFO]${NC} Your Wynncraft Explorer is now up to date.\"\n");
            writer.write("else\n");
            writer.write("    echo -e \"${YELLOW}[WARNING]${NC} Some updates failed. Check the log above.\"\n");
            writer.write("fi\n\n");

            writer.write("echo\n");
            writer.write("echo \"You can now restart Minecraft to use the updated mods.\"\n");
            writer.write("echo \"This script will auto-delete in 10 seconds...\"\n");
            writer.write("echo \"(or press Ctrl+C to exit immediately)\"\n");
            writer.write("sleep 10\n\n");

            writer.write("echo -e \"${BLUE}[INFO]${NC} Cleaning up updater...\"\n");
            writer.write("rm -- \"$0\" 2>/dev/null\n");
        }

        scriptPath.toFile().setExecutable(true);
        Main.LOGGER.info("[Updater] Created Unix update script: {}", scriptPath);
    }

    public static void executeUpdateScript() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd", "/c", "start", "\"Wynncraft Explorer Updater\"", "/wait", "we_update.bat");
            } else {
                pb = new ProcessBuilder("nohup", "/bin/bash", "we_update.sh");
            }

            pb.directory(Paths.get(".").toFile());

            if (!os.contains("win")) {
                pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            }

            Process process = pb.start();
            Main.LOGGER.info("[Updater] Update script launched successfully");

        } catch (Exception e) {
            Main.LOGGER.error("[Updater] Failed to launch update script: {}", e.getMessage());
        }
    }

    private static Path getTargetPath(UpdateManager.PendingUpdate update,
                                      Map<String, UpdateManager.InstalledMod> installedMods) {
        if ("wynncraft-explorer".equals(update.modId)) {
            return CryptoUtils.getCurrentJarPath();
        } else {
            UpdateManager.InstalledMod installedMod = installedMods.get(update.modId);
            if (installedMod != null && installedMod.filePath != null && installedMod.filePath.toFile().exists()) {
                return installedMod.filePath;
            }

            String fileName = update.modId + "-" + update.newVersion + ".jar";
            return Paths.get("mods", fileName);
        }
    }
}