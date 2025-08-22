package com.edgn.core.minecraft.system.command.commands.api;

import com.edgn.Main;
import com.edgn.core.minecraft.render.StyledText;
import com.edgn.core.minecraft.system.command.ICommand;
import com.edgn.core.minecraft.system.command.builder.Arguments;
import com.edgn.core.minecraft.system.command.builder.CommandBuilder;
import com.edgn.core.utils.StringUtil;
import com.edgn.core.wynncraft.api.ApiUtils;
import com.edgn.core.wynncraft.player.WynncraftPlayer;
import com.edgn.core.wynncraft.player.parsers.WynncraftPlayerParser;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerInformationCommand implements ICommand {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public CommandBuilder build() {
        return CommandBuilder.create("player-info")
                .description("Get any wynncraft player information")
                .aliases("pi")
                .executes(ctx -> ctx.sendError("Usage: /we player-info <player>"))
                .argument(Arguments.player("player")
                        .executes(ctx -> {
                            String name = ctx.getArgument("player", String.class);
                            if (name == null) {
                                ctx.sendError("Invalid player name provided.");
                                return;
                            }

                            scheduler.schedule(() -> {
                                try {
                                    String uuid = ApiUtils.getUUIDFromName(name);
                                    String jsonParser = ApiUtils.getWynncraftPlayerStats(uuid);
                                    WynncraftPlayer player = WynncraftPlayerParser.parsePlayer(jsonParser);

                                    if (player != null) {
                                        Formatting online = player.isOnline() ? Formatting.GREEN : Formatting.RED;

                                        String pClass = getClassChar(player);
                                        String guildName = player.getGuild() != null ? " | " + player.getGuild().getPrefix().toUpperCase() : "";

                                        ctx.sendRawMessage(Text.literal(""));

                                        String encasedText = "";
                                        if(player.getActiveCharacterObject() != null) {
                                            encasedText = StyledText.INSTANCE.encase(
                                                    player.getActiveCharacterObject().getTotalLevel()
                                                            + StringUtil.SPACE + pClass
                                                            + player.getActiveCharacterObject().getLevel() +  guildName,
                                                    Formatting.WHITE,
                                                    Formatting.DARK_GRAY);
                                        }

                                        HoverEvent hoverEvent = new HoverEvent(
                                                HoverEvent.Action.SHOW_TEXT,
                                                Text.literal(buildGuildHoverText(player)).formatted(Formatting.GRAY)
                                        );

                                        Text completeMessage = Text.literal(StringUtil.TAB_SPACE + online + "●" + Formatting.RESET + StringUtil.SPACE)
                                                .append(Text.literal(encasedText)
                                                        .styled(style -> player.getGuild() == null ? style : style.withHoverEvent(hoverEvent)))
                                                .append(Text.literal(Formatting.RESET + StringUtil.SPACE + playerStyle(player)))
                                                .append(Text.literal(Formatting.RESET + StringUtil.SPACE + Formatting.GREEN + "\uD83D\uDD59 " + player.getPlaytime()));

                                        ctx.sendRawMessage(completeMessage);

                                        ctx.sendRawMessage(Text.literal(""));
                                        ctx.sendRawMessage(getAlignedRaidStats(player));


                                    } else {
                                        ctx.sendMessage("The player was not found");
                                        Main.OVERLAY_MANAGER.getLoggerOverlay().error("Player is null", false);
                                    }
                                } catch (Exception e) {
                                    ctx.sendError("Error fetching player data: " + e.getMessage());
                                }
                            }, 1, TimeUnit.MILLISECONDS);
                        })
                );
    }

    private Text getAlignedRaidStats(WynncraftPlayer player) {
        return Text.literal(StringUtil.TAB_SPACE + StringUtil.DOUBLE_SPACE + StyledText.INSTANCE.encase("NOTG | " + player.getAllNotgCompletions(), Formatting.WHITE, Formatting.DARK_GREEN)
                + StringUtil.SPACE + StyledText.INSTANCE.encase("NOL | " + player.getAllNolCompletions(), Formatting.WHITE, Formatting.DARK_AQUA)
                + StringUtil.SPACE + StyledText.INSTANCE.encase("TCC | " + player.getAllTccCompletions(), Formatting.WHITE, Formatting.DARK_BLUE)
                + StringUtil.SPACE + StyledText.INSTANCE.encase("TNA | " + player.getAllTnaCompletions(), Formatting.WHITE, Formatting.DARK_PURPLE));
    }

    private String getClassChar(WynncraftPlayer player) {
        if(player.getActiveCharacterObject() == null) return "";
        return switch (player.getActiveCharacterObject().getType()) {
            case "ASSASSIN" -> StyledText.Embellishment.ASSASSIN.getRepresentation();
            case "MAGE" -> StyledText.Embellishment.MAGE.getRepresentation();
            case "SHAMAN" -> StyledText.Embellishment.SHAMAN.getRepresentation();
            case "WARRIOR" -> StyledText.Embellishment.WARRIOR.getRepresentation();
            default -> StyledText.Embellishment.ARCHER.getRepresentation();
        };
    }

    private String playerStyle(WynncraftPlayer player) {
        String rank = player.getSupportRank();
        String username = player.getUsername();

        StyledText.Embellishment embellishment;
        Formatting rankColor;

        if (rank == null || rank.isEmpty()) {
            embellishment = StyledText.Embellishment.NO_RANK;
            rankColor = Formatting.GRAY;
        } else {
            String rankUpper = rank.toUpperCase();

            try {
                embellishment = StyledText.Embellishment.valueOf(rankUpper);
                rankColor = embellishment.getColor();
            } catch (IllegalArgumentException e) {
                embellishment = StyledText.Embellishment.NO_RANK;
                rankColor = Formatting.GRAY;
            }
        }

        if (embellishment == StyledText.Embellishment.NO_RANK ||
                embellishment.getRepresentation().isEmpty()) {
            return rankColor + username;
        } else {
            return embellishment.getRepresentation() + StringUtil.SPACE + rankColor + username;
        }
    }

    private String buildGuildHoverText(WynncraftPlayer player) {
        if(player.getGuild() == null) return "";
        return Formatting.WHITE + playerStyle(player)
                + Formatting.WHITE + " is a "
                + StyledText.INSTANCE.encase(player.getGuild().getRank(), Formatting.WHITE, Formatting.DARK_GRAY)
                + Formatting.WHITE + " in " + Formatting.RESET
                + StyledText.INSTANCE.encase(player.getGuild().getName().toUpperCase(), Formatting.WHITE, Formatting.DARK_GRAY);
    }
}