package com.edgn.wynncraft.raid;

public enum RaidEnum {
    NOTG("Nest of the Grootslangs", "NOTG", "§2Nest of The Grootslangs"),
    NOL("Orphion's Nexus of Light", "NOL", "§f§kOrphion's Nexus of §lLight"),
    TCC("The Canyon Colossus", "TCC","The Canyon Colossus"),
    TNA("The Nameless Anomaly", "TNA", "§9§lThe §1§k§lNameless§9§l Anomaly");

    private final String raidName;
    private final String abbreviation;
    private final String entryTitle;

    RaidEnum(String raidName, String abbreviation, String entryTitle) {
        this.raidName = raidName;
        this.abbreviation = abbreviation;
        this.entryTitle = entryTitle;
    }

    public String getRaidName() {
        return raidName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getEntryTitle() {
        return entryTitle;
    }

    public IRaid createRaid() {
        return switch (this) {
            case NOTG -> Raids.NOTG;
            case NOL -> Raids.NOL;
            case TCC -> Raids.TCC;
            case TNA -> Raids.TNA;
        };
    }

    public static RaidEnum getRaidKind(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        for (RaidEnum raid : values()) {
            if (raw.contains(raid.entryTitle)) {
                return raid;
            }
        }

        return null;
    }

}
