package com.edgn.core.minecraft.system.command.commands.nbt;

import com.edgn.core.minecraft.system.command.CommandContext;
import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.utils.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;

public class GetNBTFromItemCommand implements ICommand {

    @Override
    public CommandBuilder build() {
        return CommandBuilder.create("nbt_item")
                .description("Get NBT components from held item and copy to clipboard")
                .aliases("nbt_hand")

                .executes(this::getNBTFromHeldItem);
    }

    private void getNBTFromHeldItem(CommandContext ctx) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            ctx.sendError("Player not found!");
            return;
        }

        ItemStack currentStack = player.getInventory().getStack(player.getInventory().selectedSlot);
        if (currentStack.isEmpty()) {
            ctx.sendError("No item in hand!");
            return;
        }

        ComponentMap components = currentStack.getComponents();
        if (components == null || components.isEmpty()) {
            ctx.sendError("Item has no components!");
            return;
        }

        String nbtText = "=== Item Components ===\n" +
                "Item: " + currentStack.getName().getString() + "\n" +
                "Count: " + currentStack.getCount() + "\n\n" +
                "Components:\n" +
                components;
        StringUtil.copyToClipboard(nbtText);

        ctx.sendMessage("§aCopied item components to clipboard!");
        ctx.sendMessage("§7Item: " + currentStack.getName().getString());
    }
}

