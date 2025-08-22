package com.edgn.api;

import com.edgn.api.ui.FeatureEntry;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.event.Listener;
import com.edgn.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface EdgnCoreApi {

    interface ModuleRegistry {
        boolean register(AbstractModule m);
        Set<AbstractModule> all();
    }

    interface ServiceRegistry {
        void register(IService s);
        List<IService> all();
    }

    interface UiRegistry {
        void register(FeatureEntry e);
        List<FeatureEntry> all();
    }

    interface EventAccess {
        <L extends Listener> void add(Class<L> type, L listener);
        <L extends Listener> void remove(Class<L> type, L listener);
    }

    interface CommandRegistry {
        void register(com.edgn.core.minecraft.system.command.builder.CommandBuilder builder);
        void register(com.edgn.core.minecraft.system.command.ICommand command);
        Collection<CommandBuilder> all();
        String prefix();
    }

    ModuleRegistry modules();
    ServiceRegistry services();
    UiRegistry ui();
    EventAccess events();
    CommandRegistry commands();

    String coreVersion();
}