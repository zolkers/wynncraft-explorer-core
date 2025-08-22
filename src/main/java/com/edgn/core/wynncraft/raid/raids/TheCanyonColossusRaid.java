package com.edgn.core.wynncraft.raid.raids;

import com.edgn.core.wynncraft.raid.AbstractRaid;
import com.edgn.core.wynncraft.raid.RaidBuilder;
import com.edgn.core.wynncraft.raid.RaidEnum;

import java.util.Map;

public class TheCanyonColossusRaid extends AbstractRaid {
    public static final String TWO_PLATFORMS = "2 Platforms";
    public static final String LAVA_LAKE = "Lava Lake";
    public static final String LABYRINTH = "Labyrinth";
    public static final String GOLEM_ESCORT = "Golem Escort";
    public static final String BINDING_SEAL = "Binding Seal";
    public static final String THE_CANYON_COLOSSUS = "The Canyon Colossus";

    public TheCanyonColossusRaid() {
        super(RaidEnum.TCC);
    }

    @Override
    public Map<Integer, Map<String, String>> buildRoomMap() {
        return RaidBuilder.create()
                .addLevel(1)
                .addRoom("Hold the Upper and", TWO_PLATFORMS)
                .addRoom("Use water on", LAVA_LAKE)
                .addLevel(2)
                .addRoom("Find and reach the", LABYRINTH)
                .addRoom("Wake the ancient", GOLEM_ESCORT)
                .addLevel(3)
                .addRoom("§7Activate §f4 Binding", BINDING_SEAL)
                .addLevel(4)
                .addRoom("Calm the canyon's", THE_CANYON_COLOSSUS)
                .build();
    }
}