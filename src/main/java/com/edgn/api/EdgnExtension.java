package com.edgn.api;

public interface EdgnExtension {

    String id();

    default int order() { return 0; }

    default void onLoad(EdgnCoreApi core) {}

    default void registerModules(EdgnCoreApi.ModuleRegistry registry) {}

    default void registerServices(EdgnCoreApi.ServiceRegistry registry) {}

    default void registerCommands(EdgnCoreApi.CommandRegistry registry) {}

    default void registerUi(EdgnCoreApi.UiRegistry registry) {}
}
