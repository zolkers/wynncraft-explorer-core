package com.edgn.wynncraft.raid;

import com.edgn.wynncraft.raid.raids.NestOfTheGrootslangsRaid;
import com.edgn.wynncraft.raid.raids.OrphionsNexusOfLightRaid;
import com.edgn.wynncraft.raid.raids.TheCanyonColossusRaid;
import com.edgn.wynncraft.raid.raids.TheNamelessAnomalyRaid;

public class Raids {
    public static AbstractRaid NOTG = new NestOfTheGrootslangsRaid();
    public static AbstractRaid NOL = new OrphionsNexusOfLightRaid();
    public static AbstractRaid TCC = new TheCanyonColossusRaid();
    public static AbstractRaid TNA = new TheNamelessAnomalyRaid();

}
