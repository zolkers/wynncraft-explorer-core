package com.edgn.core.module.basic;


import com.edgn.Main;
import com.edgn.annotations.Deactivated;
import com.edgn.annotations.Test;
import com.edgn.core.minecraft.system.keybinds.KeyBindingManager;
import com.edgn.core.module.basic.modules.chat.FreakyMessageBegoneModule;
import com.edgn.core.module.basic.modules.test.*;
import com.edgn.core.module.basic.modules.utility.*;
import com.google.gson.annotations.Expose;

import java.util.LinkedHashSet;
import java.util.Set;

public class ModuleManager {
    private static ModuleManager instance;
    @Expose
    private final LinkedHashSet<AbstractModule> modules = new LinkedHashSet<>();

    private ModuleManager() {
    }

    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    public Set<AbstractModule> getModules() {
        return modules;
    }

    public AbstractModule getModule(String id) {
        return modules.stream().filter(module -> module.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public AbstractModule getModule(Class<? extends AbstractModule> baseModuleClass) {
        return modules.stream().filter(module -> module.getClass().equals(baseModuleClass)).findFirst().orElse(null);
    }

    public boolean add(AbstractModule module) {
        try {
            if (module.getClass().isAnnotationPresent(com.edgn.annotations.Deactivated.class)) return false;
            if (!com.edgn.Main.TEST_MODE && module.getClass().isAnnotationPresent(com.edgn.annotations.Test.class)) return false;
            return modules.add(module);
        } catch (Exception e) {
            com.edgn.Main.LOGGER.error("Failed to add module {}: {}", module.getId(), e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractModule> T getModuleClass(Class<T> moduleClass) {
        return (T) modules.stream()
                .filter(module -> moduleClass.isAssignableFrom(module.getClass()))
                .findFirst()
                .orElse(null);
    }

    public void register() {
        try {
            modules.add(new SoundControllerModule());
            modules.add(new TestModule());
            modules.add(new FreakyMessageBegoneModule());
            modules.add(new InhibitorModule());

            modules.removeIf(module -> module.getClass().isAnnotationPresent(Deactivated.class));
            if(!Main.TEST_MODE) modules.removeIf(module -> module.getClass().isAnnotationPresent(Test.class));

        } catch (Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    public void finishModulesInitialization() {
        for (AbstractModule module : modules) {
            module.finishInitialization();
        }
    }

    public void activateAllEnabledModules() {
        for (AbstractModule module : modules) {
            module.performInitialActivation();
        }
    }

    public void rebindKeybinds() {
        KeyBindingManager.getInstance().rebindModuleActions();
    }

}


