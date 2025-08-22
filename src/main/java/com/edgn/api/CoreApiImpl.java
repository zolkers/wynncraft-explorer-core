package com.edgn.api;

import com.edgn.Main;
import com.edgn.api.ui.FeatureEntry;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleManager;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.core.minecraft.system.command.CommandManager;
import com.edgn.service.IService;
import com.edgn.service.ServiceManager;

import com.edgn.event.Listener;

import java.util.*;

public final class CoreApiImpl implements EdgnCoreApi {

    private static final class ModuleRegistryImpl implements ModuleRegistry {
        @Override public boolean register(AbstractModule m) { return ModuleManager.getInstance().add(m); }
        @Override public Set<AbstractModule> all() { return ModuleManager.getInstance().getModules(); }
    }
    private final ModuleRegistryImpl modules = new ModuleRegistryImpl();

    private static final class ServiceRegistryImpl implements ServiceRegistry {
        private boolean bootCompleted = false;
        @Override public void register(IService s) { ServiceManager.getInstance().add(s, bootCompleted); }
        @Override public List<IService> all() { return ServiceManager.getInstance().all(); }
        void markBootCompleted(){ bootCompleted = true; }
    }
    private final ServiceRegistryImpl services = new ServiceRegistryImpl();

    private static final class UiRegistryImpl implements UiRegistry {
        private final List<FeatureEntry> entries = new ArrayList<>();
        @Override public void register(FeatureEntry e) { if (e != null) entries.add(e); }
        @Override public List<FeatureEntry> all() { return Collections.unmodifiableList(entries); }
    }
    private final UiRegistryImpl ui = new UiRegistryImpl();

    private static final class EventAccessImpl implements EventAccess {
        @Override public <L extends Listener> void add(Class<L> t, L l) { Main.EVENT_MANAGER.add(t, l); }
        @Override public <L extends Listener> void remove(Class<L> t, L l) { Main.EVENT_MANAGER.remove(t, l); }
    }
    private final EventAccessImpl events = new EventAccessImpl();

    private static final class CommandRegistryImpl implements CommandRegistry {
        @Override public void register(CommandBuilder builder) { CommandManager.registerFromExtension(builder); }
        @Override public void register(ICommand command) { CommandManager.registerFromExtension(command); }
        @Override public Collection<CommandBuilder> all() { return CommandManager.getCommands(); }
        @Override public String prefix() { return CommandManager.getCommandPrefix(); }
    }
    private final CommandRegistryImpl commands = new CommandRegistryImpl();

    @Override public ModuleRegistry modules(){ return modules; }
    @Override public ServiceRegistry services(){ return services; }
    @Override public UiRegistry ui(){ return ui; }
    @Override public EventAccess events(){ return events; }
    @Override public CommandRegistry commands(){ return commands; }
    @Override public String coreVersion(){ return Main.VERSION; }

    public void markBootCompleted(){ services.markBootCompleted(); }
}
