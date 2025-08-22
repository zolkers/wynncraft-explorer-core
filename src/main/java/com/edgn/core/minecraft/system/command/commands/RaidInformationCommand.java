package com.edgn.core.minecraft.system.command.commands;

import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.service.ServiceManager;
import com.edgn.service.services.RaidEventService;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;

public class RaidInformationCommand implements ICommand {

    @Override
    public CommandBuilder build() {
        return CommandBuilder.create("current-raid-info")
                .description("Debug command for raids")
                .executes(ctx -> {
                    RaidEventService raidService = ServiceManager.getInstance().getService(RaidEventService.class);
                    ctx.sendMessage("Raid: " + raidService.getCurrentRaidType().getRaidName());
                    ctx.sendMessage("Room level: " + raidService.getLevel());
                    ctx.sendMessage("Is in intermission: " + raidService.isInIntermission());
                    ctx.sendMessage("Is in buff room: " + raidService.isInBuffRoom());
                });
    }
}