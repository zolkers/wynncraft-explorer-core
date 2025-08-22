package com.edgn.core.minecraft.system.command.builder;

import com.edgn.core.minecraft.system.command.CommandManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class EdgnLiteralArgumentBuilder extends ArgumentBuilder<LiteralArgumentBuilder<FabricClientCommandSource>> {

    public EdgnLiteralArgumentBuilder(String name) {
        super(name);
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        LiteralArgumentBuilder<FabricClientCommandSource> builder = LiteralArgumentBuilder.literal(this.name);

        if (executor != null) {
            builder.executes(ctx -> {
                executor.accept(new CommandManager.BrigadierCommandContext(ctx));
                return 1;
            });
        }

        for (ArgumentBuilder<?> child : children) {
            builder.then(child.build());
        }

        return builder;
    }
}