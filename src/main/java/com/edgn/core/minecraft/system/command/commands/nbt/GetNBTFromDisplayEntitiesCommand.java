package com.edgn.core.minecraft.system.command.commands.nbt;

import com.edgn.core.minecraft.system.command.CommandContext;
import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.core.minecraft.system.command.builder.Arguments;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.utils.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.stream.StreamSupport;

public class GetNBTFromDisplayEntitiesCommand implements ICommand {

    @Override
    public CommandBuilder build() {
        return CommandBuilder.create("nbt_displays")
                .description("Get NBT data from display entities and copy to clipboard")
                .aliases("nbt_disp")

                .executes(ctx -> {
                    getNBTFromDisplayEntities(ctx, "all");
                })

                .argument(Arguments.string("type")
                        .suggests(ctx -> new String[]{"all", "text", "item", "block"})
                        .executes(ctx -> {
                            String type = ctx.getArgument("type", String.class);
                            getNBTFromDisplayEntities(ctx, type);
                        })
                );
    }

    private void getNBTFromDisplayEntities(CommandContext ctx, String type) {
        if (MinecraftClient.getInstance().world == null) {
            ctx.sendError("World not loaded!");
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            ctx.sendError("Player not found!");
            return;
        }

        List<Entity> displayEntities = StreamSupport.stream(
                        MinecraftClient.getInstance().world.getEntities().spliterator(), false)
                .filter(entity -> {
                    if (!(entity instanceof DisplayEntity)) return false;

                    return switch (type.toLowerCase()) {
                        case "text" -> entity instanceof DisplayEntity.TextDisplayEntity;
                        case "item" -> entity instanceof DisplayEntity.ItemDisplayEntity;
                        case "block" -> entity instanceof DisplayEntity.BlockDisplayEntity;
                        default -> true;
                    };
                })
                .toList();

        if (displayEntities.isEmpty()) {
            ctx.sendMessage("§cNo " + (type.equals("all") ? "display" : type + " display") + " entities found!");
            return;
        }

        StringBuilder nbtBuilder = new StringBuilder();
        nbtBuilder.append("=== NBT Data for ").append(displayEntities.size()).append(" ").append(type).append(" display entities ===\n\n");

        for (int i = 0; i < displayEntities.size(); i++) {
            Entity entity = displayEntities.get(i);
            NbtCompound nbtData = new NbtCompound();
            String nbtString = entity.writeNbt(nbtData).toString();

            nbtBuilder.append("Display Entity ").append(i + 1).append(" (").append(entity.getType().getName().getString()).append("):\n");
            nbtBuilder.append(nbtString).append("\n\n");
        }

        StringUtil.copyToClipboard(nbtBuilder.toString());

        ctx.sendMessage("§aCopied NBT data for " + displayEntities.size() + " display entities to clipboard!");
        ctx.sendMessage("§7Type filter: " + type);
    }
}