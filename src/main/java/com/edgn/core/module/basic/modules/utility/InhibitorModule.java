package com.edgn.core.module.basic.modules.utility;

import com.edgn.Main;
import com.edgn.annotations.DefaultEnabled;
import com.edgn.core.config.ConfigManager;
import com.edgn.event.listeners.wynntils.DataCrowdSourcingMessageFeatureListener;
import com.edgn.event.listeners.wynntils.HadesServiceListener;
import com.edgn.event.listeners.wynntils.TelemetryMessageFeatureListener;
import com.edgn.core.module.basic.AbstractModule;
import com.edgn.core.module.basic.ModuleCategory;
import com.edgn.core.module.basic.ModuleInfo;
import com.edgn.core.module.settings.SettingsGroup;
import com.google.gson.annotations.Expose;

@DefaultEnabled
@ModuleInfo(
        name = "Inhibitor",
        description = "My data my game, wynntils BEGONE",
        category = ModuleCategory.UTILITY,
        authors = {"EDGN"},
        version = "1.0.1"
)
public class InhibitorModule extends AbstractModule implements HadesServiceListener,
        DataCrowdSourcingMessageFeatureListener, TelemetryMessageFeatureListener {

    @ConfigManager.SaveField @Expose private boolean removeHadesConnection = true;
    @ConfigManager.SaveField @Expose private boolean removeCrowdSourcingMessage = true;
    @ConfigManager.SaveField @Expose private boolean removeTelemetryMessage = true;

    public InhibitorModule() {
        super("Inhibitor");
    }

    @Override
    protected void initializeSettings() {
        SettingsGroup mainGroup = new SettingsGroup("Connections", "You won't get my data wynntils !");

        mainGroup.addBoolean("Remove Hades Connection", "Remove hades server connection service protocol", removeHadesConnection)
                .setOnValueChanged((oldValue, newValue) -> this.removeHadesConnection = (Boolean) newValue);

        SettingsGroup chatGroup = new SettingsGroup("Chat", "Remove the spam in the chat from wynntils !");

        chatGroup.addBoolean("Remove crowd sourcing chat bloat", "Remove this annoying message", removeCrowdSourcingMessage)
                .setOnValueChanged((oldValue, newValue) -> this.removeCrowdSourcingMessage = (Boolean) newValue);

        chatGroup.addBoolean("Remove telemetry chat bloat", "Remove this annoying message", removeTelemetryMessage)
                .setOnValueChanged((oldValue, newValue) -> this.removeTelemetryMessage = (Boolean) newValue);

        this.settingsGroups.add(mainGroup);
        this.settingsGroups.add(chatGroup);
    }

    @Override
    public void onHaderService(HadesServiceEvent event) {
        if(this.removeHadesConnection) event.cancel();
    }

    @Override
    public void onCrowdSourcingMessage(DataCrowdSourcingMessageFeatureEvent event) {
        if(this.removeCrowdSourcingMessage) event.cancel();
    }

    @Override
    public void onTelemetryMessage(TelemetryMessageFeatureEvent event) {
        if(this.removeTelemetryMessage) event.cancel();
    }

    @Override
    protected void onEnable() {
        Main.EVENT_MANAGER.add(HadesServiceListener.class, this);
        Main.EVENT_MANAGER.add(DataCrowdSourcingMessageFeatureListener.class, this);
        Main.EVENT_MANAGER.add(TelemetryMessageFeatureListener.class, this);
    }

    @Override
    protected void onDisable() {
        Main.EVENT_MANAGER.remove(HadesServiceListener.class, this);
        Main.EVENT_MANAGER.remove(DataCrowdSourcingMessageFeatureListener.class, this);
        Main.EVENT_MANAGER.remove(TelemetryMessageFeatureListener.class, this);
    }

    @Override
    public void onSettingsChanged() {

    }
}
