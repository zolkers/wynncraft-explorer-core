package com.edgn.core.minecraft.ui.screens.terminal;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

import java.lang.reflect.*;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;

public class TerminalContext {
    private String currentDirectory;
    private Map<String, String> environment;
    private Map<String, String> aliases;
    private List<String> commandHistory;
    private Path rootPath;
    private long startTime;
    private int textColor = 0xFFFFFFFF;        
    private int backgroundColor = 0xFF000000;   
    private int borderColor = 0xFF333333;      
    private int scrollbarColor = 0xFF555555;   
    private int scrollbarThumbColor = 0xFF888888;

    private String currentNamespace;
    private Class<?> currentClass;
    private Object currentInstance;
    private Map<String, Class<?>> loadedClasses;

    public TerminalContext() {
        this.environment = new HashMap<>();
        this.aliases = new HashMap<>();
        this.commandHistory = new ArrayList<>();
        this.startTime = System.currentTimeMillis();
        this.loadedClasses = new HashMap<>();

        this.rootPath = FabricLoader.getInstance().getGameDir();
        this.currentNamespace = "file";

        
        environment.put("USER", System.getProperty("user.name", "minecraft"));
        environment.put("HOME", "/");
        environment.put("PATH", "/bin:/usr/bin:/usr/local/bin");
        environment.put("SHELL", "/bin/bash");
        environment.put("TERM", "xterm-256color");
        environment.put("FABRIC_ROOT", rootPath.toString());

        this.currentDirectory = "/";
        environment.put("PWD", this.currentDirectory);

        
        loadCommonClasses();
    }

    private void loadCommonClasses() {
        try {
            loadedClasses.put("MinecraftClient", MinecraftClient.class);
            loadedClasses.put("GameOptions", GameOptions.class);
            loadedClasses.put("FabricLoader", FabricLoader.class);
            loadedClasses.put("World", World.class);
            loadedClasses.put("PlayerEntity", PlayerEntity.class);
            loadedClasses.put("BlockPos", BlockPos.class);
            loadedClasses.put("Vec3d", Vec3d.class);
            loadedClasses.put("ItemStack", ItemStack.class);


        } catch (Exception ignored) {}
    }

    
    public String getCurrentDirectory() { return currentDirectory; }
    public void setCurrentDirectory(String dir) {
        this.currentDirectory = dir;
        environment.put("PWD", dir);
    }

    public Map<String, String> getEnvironment() { return environment; }
    public Map<String, String> getAliases() { return aliases; }
    public List<String> getCommandHistory() { return commandHistory; }
    public long getStartTime() { return startTime; }

    
    public String getCurrentNamespace() { return currentNamespace; }
    public void setCurrentNamespace(String namespace) { this.currentNamespace = namespace; }

    public Class<?> getCurrentClass() { return currentClass; }
    public void setCurrentClass(Class<?> clazz) {
        this.currentClass = clazz;
        this.currentNamespace = "class";
    }

    public Object getCurrentInstance() { return currentInstance; }
    public void setCurrentInstance(Object instance) {
        this.currentInstance = instance;
        if (instance != null) {
            this.currentClass = instance.getClass();
        }
    }

    public Map<String, Class<?>> getLoadedClasses() { return loadedClasses; }

    
    public Class<?> loadClass(String className) {
        try {
            
            Class<?> clazz = Class.forName(className);
            loadedClasses.put(clazz.getSimpleName(), clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            
            String[] commonPackages = {
                    "net.minecraft.client.",
                    "net.minecraft.world.",
                    "net.minecraft.entity.",
                    "net.minecraft.item.",
                    "net.minecraft.block.",
                    "net.minecraft.util.",
                    "net.fabricmc.",
                    "com.mojang."
            };

            for (String pkg : commonPackages) {
                try {
                    Class<?> clazz = Class.forName(pkg + className);
                    loadedClasses.put(clazz.getSimpleName(), clazz);
                    return clazz;
                } catch (ClassNotFoundException ignored) {}
            }

            return null;
        }
    }

    
    public List<String> getAvailableClasses(String packagePrefix) {
        List<String> classes = new ArrayList<>();

        try {
            
            for (String className : loadedClasses.keySet()) {
                if (packagePrefix == null || className.toLowerCase().contains(packagePrefix.toLowerCase())) {
                    classes.add(loadedClasses.get(className).getName());
                }
            }

            
            int maxClasses = 100; 
            for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
                if (classes.size() >= maxClasses) break;

                Path modPath = mod.getOrigin().getPaths().get(0);
                if (Files.isRegularFile(modPath) && modPath.toString().endsWith(".jar")) {
                    try (JarFile jarFile = new JarFile(modPath.toFile())) {
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements() && classes.size() < maxClasses) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();

                            if (name.endsWith(".class") && !name.contains("$")) {
                                String className = name.replace('/', '.').replace(".class", "");
                                if (packagePrefix == null || className.startsWith(packagePrefix)) {
                                    classes.add(className);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error scanning classes: " + e.getMessage());
        }

        return classes.stream().distinct().sorted().collect(java.util.stream.Collectors.toList());
    }

    
    public Object getInstanceOf(Class<?> clazz) {
        try {
            
            if (clazz == MinecraftClient.class) {
                return MinecraftClient.getInstance();
            }

            
            if (clazz == FabricLoader.class) {
                return FabricLoader.getInstance();
            }

            
            try {
                Method getInstance = clazz.getMethod("getInstance");
                if (Modifier.isStatic(getInstance.getModifiers())) {
                    return getInstance.invoke(null);
                }
            } catch (NoSuchMethodException ignored) {}

            
            try {
                Method get = clazz.getMethod("get");
                if (Modifier.isStatic(get.getModifiers())) {
                    return get.invoke(null);
                }
            } catch (NoSuchMethodException ignored) {}

            
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception ignored) {}

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    
    public Path getRealPath(String virtualPath) {
        if (virtualPath.equals("/")) {
            return rootPath;
        }
        String relativePath = virtualPath.startsWith("/") ? virtualPath.substring(1) : virtualPath;
        return rootPath.resolve(relativePath);
    }

    public boolean realDirectoryExists(String virtualPath) {
        try {
            Path realPath = getRealPath(virtualPath);
            return Files.exists(realPath) && Files.isDirectory(realPath);
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getRealDirectoryEntries(String virtualPath, boolean directoriesOnly) {
        List<String> entries = new ArrayList<>();

        try {
            Path realPath = getRealPath(virtualPath);
            if (Files.exists(realPath) && Files.isDirectory(realPath)) {
                Files.list(realPath).forEach(path -> {
                    String name = path.getFileName().toString();
                    boolean isDir = Files.isDirectory(path);

                    if (!directoriesOnly || isDir) {
                        entries.add(name);
                    }
                });
            }
        } catch (Exception ignored) {
            
        }

        return entries;
    }

    public int getTextColor() { return textColor; }
    public void setTextColor(int textColor) { this.textColor = textColor; }

    public int getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }

    public int getBorderColor() { return borderColor; }
    public void setBorderColor(int borderColor) { this.borderColor = borderColor; }

    public int getScrollbarColor() { return scrollbarColor; }
    public void setScrollbarColor(int scrollbarColor) {
        this.scrollbarColor = scrollbarColor;
        
        this.scrollbarThumbColor = adjustBrightness(scrollbarColor, 1.5f);
    }

    public int getScrollbarThumbColor() { return scrollbarThumbColor; }
    public void setScrollbarThumbColor(int scrollbarThumbColor) { this.scrollbarThumbColor = scrollbarThumbColor; }

    
    private int adjustBrightness(int color, float factor) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = Math.max(0, Math.min(255, (int) (r * factor)));
        g = Math.max(0, Math.min(255, (int) (g * factor)));
        b = Math.max(0, Math.min(255, (int) (b * factor)));

        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    @Deprecated
    public Map<String, Object> getFilesystem() {
        return new HashMap<>();
    }
}