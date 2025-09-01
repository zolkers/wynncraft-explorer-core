package com.edgn;

import com.edgn.api.EdgnExtension;
import com.edgn.core.updater.UpdateManager;
import com.edgn.service.ServiceManager;
import com.edgn.core.minecraft.system.command.CommandManager;
import com.edgn.core.config.ConfigManager;
import com.edgn.event.EventManager;
import com.edgn.event.fabric.FabricRenderEvent;
import com.edgn.event.fabric.FabricTickEvent;
import com.edgn.event.fabric.HudRenderer;
import com.edgn.core.minecraft.system.keybinds.KeyBindingManager;
import com.edgn.core.minecraft.system.keybinds.MinecraftKeybinds;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleManager;
import com.edgn.core.minecraft.ui.overlays.OverlayManager;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edgn.api.CoreApiImpl;
import com.edgn.api.EdgnCoreApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

public final class Main implements ModInitializer {
	public static final String MOD_ID = "wynncraft-explorer";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String VERSION = "V1.3";
	public static final EventManager EVENT_MANAGER = new EventManager();
	public static final OverlayManager OVERLAY_MANAGER  = OverlayManager.getInstance();
	public static final boolean TEST_MODE = true;

	private static final CoreApiImpl CORE_API = new CoreApiImpl();
	public static EdgnCoreApi getCoreApi() { return CORE_API; }

	@Override
	public void onInitialize() {
		this.registerEvents();
		ConfigManager.init();
		ModuleManager.getInstance().register();

		loadExtensions();

		KeyBindingManager.getInstance().init();
		AbstractModule.initSaveManager();
		AbstractModule.loadAllModules();

		ModuleManager.getInstance().finishModulesInitialization();
		ModuleManager.getInstance().activateAllEnabledModules();
		ModuleManager.getInstance().rebindKeybinds();

		MinecraftKeybinds.initialize();
		CommandManager.init();
		ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
		Main.OVERLAY_MANAGER.getLoggerOverlay().action("You are running Wynncraft-Explorer " + Main.VERSION, false);
		UpdateManager.getInstance().checkUpdatesOnStartup();

	}

	private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher,
								  CommandRegistryAccess registryAccess) {
		CommandManager.registerBrigadierCommands(dispatcher);
	}

	private void registerEvents() {
		WorldRenderEvents.LAST.register(new FabricRenderEvent());
		ClientTickEvents.START_CLIENT_TICK.register(new FabricTickEvent());
		ClientTickEvents.END_CLIENT_TICK.register(client -> MinecraftKeybinds.onKeyPressed(MinecraftClient.getInstance()));
		HudRenderer.register();
		ServiceManager.getInstance().register();
	}

	private void loadExtensions() {
		try {
			ServiceLoader<EdgnExtension> loader = ServiceLoader.load(EdgnExtension.class);
			List<EdgnExtension> exts = new ArrayList<>();
			loader.forEach(exts::add);

			exts.sort(Comparator.comparingInt(EdgnExtension::order));
			LOGGER.info("Found {} extensions: {}", exts.size(),
					exts.stream().map(EdgnExtension::id).toList());

			CORE_API.markBootCompleted();

			extensionsEntryPoints(exts);
		} catch (Throwable t) {
			LOGGER.error("Extension discovery failed: {}", t.toString());
		}
	}

	private static void extensionsEntryPoints(List<EdgnExtension> exts) {
		for (EdgnExtension ext : exts) {
			try { ext.onLoad(CORE_API); } catch (Throwable t) {
				LOGGER.error("[Ext:{}] onLoad failed: {}", ext.id(), t.toString());
			}
			try { ext.registerModules(CORE_API.modules()); } catch (Throwable t) {
				LOGGER.error("[Ext:{}] registerModules failed: {}", ext.id(), t.toString());
			}
			try { ext.registerCommands(CORE_API.commands()); } catch (Throwable t) {
				LOGGER.error("[Ext:{}] registerCommands failed: {}", ext.id(), t.toString());
			}
			try { ext.registerServices(CORE_API.services()); } catch (Throwable t) {
				LOGGER.error("[Ext:{}] registerServices failed: {}", ext.id(), t.toString());
			}
			try { ext.registerUi(CORE_API.ui()); } catch (Throwable t) {
				LOGGER.error("[Ext:{}] registerUi failed: {}", ext.id(), t.toString());
			}
		}
	}


}
