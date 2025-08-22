package com.edgn.wynncraft.raid.raids;

import com.edgn.wynncraft.raid.AbstractRaid;
import com.edgn.wynncraft.raid.RaidBuilder;
import com.edgn.wynncraft.raid.RaidEnum;

import java.util.Map;

public class TheNamelessAnomalyRaid extends AbstractRaid {

    public static final String FLOODING_CANYON = "Flooding Canyon";
    public static final String SUNKEN_GROTTO = "Sunken Grotto";
    public static final String NAMELESS_CAVE = "Nameless Cave";
    public static final String WEEPING_SOULROOT = "Weeping Soulroot";
    public static final String BLUESHIFT_WILDS = "Blueshift Wilds";
    public static final String TWISTED_JUNGLE = "Twisted Jungle";
    public static final String THE_NAMELESS_ANOMALY = "The ##### Anomaly";

    public TheNamelessAnomalyRaid() {
        super(RaidEnum.TNA);
    }

    @Override
    protected Map<Integer, Map<String, String>> buildRoomMap() {
        return RaidBuilder.create()
                .addLevel(1)
                .addRoom("One player must take", FLOODING_CANYON)
                .addRoom("Hold the stump for", SUNKEN_GROTTO)
                .addLevel(2)
                .addRoom("Find and kill", NAMELESS_CAVE)
                .addRoom("Offer souls to the", WEEPING_SOULROOT)
                .addLevel(3)
                .addRoom("Protect the Bulb", BLUESHIFT_WILDS)
                .addRoom("§7Collect §f5 Void Matter", TWISTED_JUNGLE)
                .addLevel(4)
                .addRoom("Survive.", THE_NAMELESS_ANOMALY)
                .build();
    }
}