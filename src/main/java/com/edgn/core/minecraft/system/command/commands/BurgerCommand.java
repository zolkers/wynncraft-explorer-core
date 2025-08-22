package com.edgn.core.minecraft.system.command.commands;

import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.core.minecraft.system.command.builder.Arguments;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;

public class BurgerCommand implements ICommand {

    @Override
    public CommandBuilder build() {
        return CommandBuilder.create("burger")
                .description("Get the tasty burger buuuuuild")
                .aliases("b")

                .executes(ctx -> {
                    ctx.sendMessage("https://wynnbuilder.github.io/builder/#10_10N11Y09W0QN0EE0EE0Jn0OP0Qb1I000022111g1000CO1000CO1000CO1000CO1006CO0z0z0+0+0+0+0-1T2Y2Y2Z2Z2a2azs+3Sz97A0");
                })

                .argument(Arguments.literal("classic")
                        .executes(ctx -> {
                            ctx.sendMessage("https://wynnbuilder.github.io/builder/#10_10N11Y09W0QN0EE0EE0Jn0OP0Qb1I000022111g1000CO1000CO1000CO1000CO1006CO0z0z0+0+0+0+0-1T2Y2Y2Z2Z2a2azs+3Sz97A0");
                        })
                )

                .argument(Arguments.literal("spicy")
                        .executes(ctx -> {
                            ctx.getPlayer().networkHandler.sendChatCommand("g https://wynnbuilder.github.io/builder/#10_10N11Y09W0QN0EE0EE0Jn0OP0Qb1I000022111g1000CO1000CO1000CO1000CO1006CO0z0z0+0+0+0+0-1T2Y2Y2Z2Z2a2azs+3Sz97A0");
                        })
                )

                .argument(Arguments.literal("bigmac")
                        .executes(ctx -> {
                            ctx.getPlayer().networkHandler.sendChatCommand("p https://wynnbuilder.github.io/builder/#10_10N11Y09W0QN0EE0EE0Jn0OP0Qb1I000022111g1000CO1000CO1000CO1000CO1006CO0z0z0+0+0+0+0-1T2Y2Y2Z2Z2a2azs+3Sz97A0");
                        })
                )

                .argument(Arguments.literal("whopper")
                        .executes(ctx -> {
                            ctx.sendError("Usage: /we burger whopper <player>");
                        })
                        .then(Arguments.player("player")
                                .executes(ctx -> {
                                    String player = ctx.getArgument("player", String.class);
                                    ctx.getPlayer().networkHandler.sendChatCommand("msg " + player + " https://wynnbuilder.github.io/builder/#10_10N11Y09W0QN0EE0EE0Jn0OP0Qb1I000022111g1000CO1000CO1000CO1000CO1006CO0z0z0+0+0+0+0-1T2Y2Y2Z2Z2a2azs+3Sz97A0");
                                })
                        )
                );
    }
}