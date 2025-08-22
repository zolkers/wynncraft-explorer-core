package com.edgn.wynncraft.player.statistics;

import java.util.List;
import java.util.Map;

public class Character {
    private String type;
    private String reskin;
    private String nickname;
    private int level;
    private long xp;
    private int xpPercent;
    private int totalLevel;
    private int wars;
    private double playtime;
    private int mobsKilled;
    private int chestsFound;
    private int itemsIdentified;
    private long blocksWalked;
    private int logins;
    private int deaths;
    private int discoveries;
    private Object preEconomy;
    private PvpStats pvp;
    private List<String> gamemode;
    private Map<String, Integer> skillPoints;
    private Map<String, ProfessionLevel> professions;
    private GameStats dungeons;
    private GameStats raids;
    private List<String> quests;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReskin() { return reskin; }
    public void setReskin(String reskin) { this.reskin = reskin; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public long getXp() { return xp; }
    public void setXp(long xp) { this.xp = xp; }

    public int getXpPercent() { return xpPercent; }
    public void setXpPercent(int xpPercent) { this.xpPercent = xpPercent; }

    public int getTotalLevel() { return totalLevel; }
    public void setTotalLevel(int totalLevel) { this.totalLevel = totalLevel; }

    public int getWars() { return wars; }
    public void setWars(int wars) { this.wars = wars; }

    public double getPlaytime() { return playtime; }
    public void setPlaytime(double playtime) { this.playtime = playtime; }

    public int getMobsKilled() { return mobsKilled; }
    public void setMobsKilled(int mobsKilled) { this.mobsKilled = mobsKilled; }

    public int getChestsFound() { return chestsFound; }
    public void setChestsFound(int chestsFound) { this.chestsFound = chestsFound; }

    public int getItemsIdentified() { return itemsIdentified; }
    public void setItemsIdentified(int itemsIdentified) { this.itemsIdentified = itemsIdentified; }

    public long getBlocksWalked() { return blocksWalked; }
    public void setBlocksWalked(long blocksWalked) { this.blocksWalked = blocksWalked; }

    public int getLogins() { return logins; }
    public void setLogins(int logins) { this.logins = logins; }

    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }

    public int getDiscoveries() { return discoveries; }
    public void setDiscoveries(int discoveries) { this.discoveries = discoveries; }

    public Object getPreEconomy() { return preEconomy; }
    public void setPreEconomy(Object preEconomy) { this.preEconomy = preEconomy; }

    public PvpStats getPvp() { return pvp; }
    public void setPvp(PvpStats pvp) { this.pvp = pvp; }

    public List<String> getGamemode() { return gamemode; }
    public void setGamemode(List<String> gamemode) { this.gamemode = gamemode; }

    public Map<String, Integer> getSkillPoints() { return skillPoints; }
    public void setSkillPoints(Map<String, Integer> skillPoints) { this.skillPoints = skillPoints; }

    public Map<String, ProfessionLevel> getProfessions() { return professions; }
    public void setProfessions(Map<String, ProfessionLevel> professions) { this.professions = professions; }

    public GameStats getDungeons() { return dungeons; }
    public void setDungeons(GameStats dungeons) { this.dungeons = dungeons; }

    public GameStats getRaids() { return raids; }
    public void setRaids(GameStats raids) { this.raids = raids; }

    public List<String> getQuests() { return quests; }
    public void setQuests(List<String> quests) { this.quests = quests; }
}
