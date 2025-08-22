package com.edgn.core.minecraft.system.command.commands;

import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.core.minecraft.system.command.builder.Arguments;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.wynncraft.api.ApiUtils;

public class GetUUIDFromNameCommand implements ICommand {

    @Override
    public CommandBuilder build() {
        return CommandBuilder.create("getuuidfromname")
                .description("Get a player's UUID from their name.")
                .aliases("uuid")
                .executes(ctx -> {
                    ctx.sendError("Usage: /we getuuidfromname <player>");
                })
                .argument(Arguments.player("player")
                        .executes(ctx -> {
                            String playerName = ctx.getArgument("player", String.class);
                            if (playerName != null) {
                                ctx.sendMessage(ApiUtils.getUUIDFromName(playerName));
                            }
                        })
                );
    }
}