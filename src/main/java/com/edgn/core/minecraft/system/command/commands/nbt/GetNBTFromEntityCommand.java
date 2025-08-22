package com.edgn.core.minecraft.system.command.commands.nbt;

import com.edgn.core.minecraft.system.command.CommandContext;
import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.core.minecraft.system.command.builder.Arguments;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.utils.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class GetNBTFromEntityCommand implements ICommand {

    @Override
    public CommandBuilder build() {
        return CommandBuilder.create("nbt_entities")
                .description("Get NBT data from nearby entitie to clipboard")
                .aliases("nbt_ent")

                .executes(ctx -> {
                    getNBTFromEntities(ctx, 50.0);
                })

                .argument(Arguments.integer("range", 1, 100)
                        .executes(ctx -> {
                            int range = ctx.getArgument("range", Integer.class);
                            getNBTFromEntities(ctx, (double) range);
                        })
                );
    }

    private void getNBTFromEntities(CommandContext ctx, double detectionRange) {
        if (MinecraftClient.getInstance().world == null) {
            ctx.sendError("World not loaded!");
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            ctx.sendError("Player not found!");
            return;
        }

        Vec3d playerPos = player.getPos();
        Box detectionBox = new Box(
                playerPos.x - detectionRange, playerPos.y - detectionRange, playerPos.z - detectionRange,
                playerPos.x + detectionRange, playerPos.y + detectionRange, playerPos.z + detectionRange
        );

        List<Entity> entities = MinecraftClient.getInstance().world.getEntitiesByClass(
                Entity.class,
                detectionBox,
                entity -> true
        );

        if (entities.isEmpty()) {
            ctx.sendMessage("§cNo entities found in range " + (int)detectionRange + " blocks!");
            return;
        }

        StringBuilder nbtDataStringBuilder = new StringBuilder();
        nbtDataStringBuilder.append("=== NBT Data for ").append(entities.size()).append(" entities ===\n\n");

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            NbtCompound nbtData = new NbtCompound();
            String entityNbt = entity.writeNbt(nbtData).toString();

            nbtDataStringBuilder.append("Entity ").append(i + 1).append(" (").append(entity.getType().getName().getString()).append("):\n");
            nbtDataStringBuilder.append(entityNbt).append("\n\n");
        }

        String nbtDataString = nbtDataStringBuilder.toString();
        StringUtil.copyToClipboard(nbtDataString);

        ctx.sendMessage("§aCopied NBT data for " + entities.size() + " entities to clipboard!");
        ctx.sendMessage("§7Range: " + (int)detectionRange + " blocks");
    }
}