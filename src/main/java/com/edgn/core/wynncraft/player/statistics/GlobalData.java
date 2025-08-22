package com.edgn.core.wynncraft.player.statistics;

public class GlobalData {
    private int wars;
    private int totalLevel;
    private int killedMobs;
    private int chestsFound;
    private GameStats dungeons;
    private GameStats raids;
    private int completedQuests;
    private PvpStats pvp;

    public int getWars() { return wars; }
    public void setWars(int wars) { this.wars = wars; }

    public int getTotalLevel() { return totalLevel; }
    public void setTotalLevel(int totalLevel) { this.totalLevel = totalLevel; }

    public int getKilledMobs() { return killedMobs; }
    public void setKilledMobs(int killedMobs) { this.killedMobs = killedMobs; }

    public int getChestsFound() { return chestsFound; }
    public void setChestsFound(int chestsFound) { this.chestsFound = chestsFound; }

    public GameStats getDungeons() { return dungeons; }
    public void setDungeons(GameStats dungeons) { this.dungeons = dungeons; }

    public GameStats getRaids() { return raids; }
    public void setRaids(GameStats raids) { this.raids = raids; }

    public int getCompletedQuests() { return completedQuests; }
    public void setCompletedQuests(int completedQuests) { this.completedQuests = completedQuests; }

    public PvpStats getPvp() { return pvp; }
    public void setPvp(PvpStats pvp) { this.pvp = pvp; }
}