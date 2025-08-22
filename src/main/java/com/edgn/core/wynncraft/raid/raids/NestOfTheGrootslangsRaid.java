package com.edgn.core.wynncraft.raid.raids;

import com.edgn.core.wynncraft.raid.AbstractRaid;
import com.edgn.core.wynncraft.raid.RaidBuilder;
import com.edgn.core.wynncraft.raid.RaidEnum;

import java.util.Map;

public class NestOfTheGrootslangsRaid extends AbstractRaid {

    public static final String SLIMEY_PLATFORM = "Slimey Platform";
    public static final String TOWER_DEFENSE = "Tower Defense";
    public static final String SLIME_GATHERING = "Slime Gathering";
    public static final String TUNNEL_TRAVERSAL = "Tunnel Traversal";
    public static final String MINIBOSSES = "Minibosses";
    public static final String GROOTSLANG_WYRMLING = "Grootslang Wyrmling";

    public NestOfTheGrootslangsRaid() {
        super(RaidEnum.NOTG);
    }
    @Override
    public Map<Integer, Map<String, String>> buildRoomMap() {
        return RaidBuilder.create()
                .addLevel(1)
                .addRoom("Hold the platform", SLIMEY_PLATFORM)
                .addRoom("Hold and defend", TOWER_DEFENSE)
                .addLevel(2)
                .addRoom("§7Collect §f10 Slimy Goo", SLIME_GATHERING)
                .addLevel(3)
                .addRoom("Have a player pick up", TUNNEL_TRAVERSAL)
                .addRoom("§f2 players §7must", MINIBOSSES)
                .addRoom("§7Slay §fminibosses §7to", MINIBOSSES)
                .addLevel(4)
                .addRoom("Slay the Restless", GROOTSLANG_WYRMLING)
                .build();
    }
}