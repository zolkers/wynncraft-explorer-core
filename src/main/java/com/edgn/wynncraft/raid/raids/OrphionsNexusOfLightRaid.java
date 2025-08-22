package com.edgn.wynncraft.raid.raids;

import com.edgn.wynncraft.raid.AbstractRaid;
import com.edgn.wynncraft.raid.RaidBuilder;
import com.edgn.wynncraft.raid.RaidEnum;

import java.util.Map;

public class OrphionsNexusOfLightRaid extends AbstractRaid {

    public static final String DECAYING_TOWER = "Decaying Tower";
    public static final String CLOUD_DECAY = "Cloud Decay";
    public static final String LIGHT_GATHERING = "Light Gathering";
    public static final String LIGHT_TOWER = "Light Tower";
    public static final String INVISIBLE_MAZE = "Invisible Maze";
    public static final String ORPHION = "Orphion";
    public static final String THE_PARASITE = "The Parasite";

    public OrphionsNexusOfLightRaid() {
        super(RaidEnum.NOL);
    }

    @Override
    public Map<Integer, Map<String, String>> buildRoomMap() {
        return RaidBuilder.create()
                .addLevel(1)
                .addRoom("Hold the tower", DECAYING_TOWER)
                .addLevel(2)
                .addRoom("Kill all Crystalline", CLOUD_DECAY)
                .addRoom("§7Collect §f10 Light", LIGHT_GATHERING)
                .addLevel(3)
                .addRoom("Purify the decaying", LIGHT_TOWER)
                .addRoom("Escort your party to", INVISIBLE_MAZE)
                .addLevel(4)
                .addRoom("Save Him.", ORPHION)
                .addLevel(5)
                .addRoom("Finish that which He", THE_PARASITE)
                .build();
    }

}