package com.edgn.core.wynncraft.player;

import com.edgn.core.wynncraft.player.statistics.GlobalData;
import com.edgn.core.wynncraft.player.statistics.Guild;
import com.edgn.core.wynncraft.player.statistics.RankColor;
import com.edgn.core.wynncraft.player.statistics.Rankings;
import com.edgn.core.wynncraft.player.statistics.Character;

import java.util.Map;

public class WynncraftPlayer {
    private String username;
    private boolean online;
    private String server;
    private String activeCharacter;
    private String uuid;
    private String rank;
    private String rankBadge;
    private RankColor legacyRankColour;
    private String shortenedRank;
    private String supportRank;
    private Boolean veteran;
    private String firstJoin;
    private String lastJoin;
    private double playtime;
    private Guild guild;
    private GlobalData globalData;
    private Integer forumLink;
    private Rankings ranking;
    private Rankings previousRanking;
    private boolean publicProfile;
    private Map<String, Character> characters;

    private static final String RAID_NAMELESS_ANOMALY = "The Nameless Anomaly";
    private static final String RAID_NEST_OF_GROOTSLANGS = "Nest of the Grootslangs";
    private static final String RAID_ORPHION_NEXUS = "Orphion's Nexus of Light";
    private static final String RAID_CANYON_COLOSSUS = "The Canyon Colossus";

    public int getAllTnaCompletions() {
        int total = 0;

        if (globalData != null && globalData.getRaids() != null &&
                globalData.getRaids().getList() != null &&
                globalData.getRaids().getList().containsKey(RAID_NAMELESS_ANOMALY)) {
            total = globalData.getRaids().getList().get(RAID_NAMELESS_ANOMALY);
        }

        return total;
    }

    public int getAllNotgCompletions() {
        int total = 0;

        if (globalData != null && globalData.getRaids() != null &&
                globalData.getRaids().getList() != null &&
                globalData.getRaids().getList().containsKey(RAID_NEST_OF_GROOTSLANGS)) {
            total = globalData.getRaids().getList().get(RAID_NEST_OF_GROOTSLANGS);
        }

        return total;
    }

    public int getAllNolCompletions() {
        int total = 0;

        if (globalData != null && globalData.getRaids() != null &&
                globalData.getRaids().getList() != null &&
                globalData.getRaids().getList().containsKey(RAID_ORPHION_NEXUS)) {
            total = globalData.getRaids().getList().get(RAID_ORPHION_NEXUS);
        }

        return total;
    }

    public int getAllTccCompletions() {
        int total = 0;

        if (globalData != null && globalData.getRaids() != null &&
                globalData.getRaids().getList() != null &&
                globalData.getRaids().getList().containsKey(RAID_CANYON_COLOSSUS)) {
            total = globalData.getRaids().getList().get(RAID_CANYON_COLOSSUS);
        }

        return total;
    }


    public int getAllRaidCompletions() {
        int total = 0;

        if (globalData != null && globalData.getRaids() != null) {
            total = globalData.getRaids().getTotal();
        }

        return total;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isOnline() { return online; }
    public void setOnline(boolean online) { this.online = online; }

    public String getServer() { return server; }
    public void setServer(String server) { this.server = server; }

    public String getActiveCharacter() { return activeCharacter; }
    public void setActiveCharacter(String activeCharacter) { this.activeCharacter = activeCharacter; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public String getRankBadge() { return rankBadge; }
    public void setRankBadge(String rankBadge) { this.rankBadge = rankBadge; }

    public RankColor getLegacyRankColour() { return legacyRankColour; }
    public void setLegacyRankColour(RankColor legacyRankColour) { this.legacyRankColour = legacyRankColour; }

    public String getShortenedRank() { return shortenedRank; }
    public void setShortenedRank(String shortenedRank) { this.shortenedRank = shortenedRank; }

    public String getSupportRank() { return supportRank; }
    public void setSupportRank(String supportRank) { this.supportRank = supportRank; }

    public Boolean getVeteran() { return veteran; }
    public void setVeteran(Boolean veteran) { this.veteran = veteran; }

    public String getFirstJoin() { return firstJoin; }
    public void setFirstJoin(String firstJoin) { this.firstJoin = firstJoin; }

    public String getLastJoin() { return lastJoin; }
    public void setLastJoin(String lastJoin) { this.lastJoin = lastJoin; }

    public double getPlaytime() { return playtime; }
    public void setPlaytime(double playtime) { this.playtime = playtime; }

    public Guild getGuild() { return guild; }
    public void setGuild(Guild guild) { this.guild = guild; }

    public GlobalData getGlobalData() { return globalData; }
    public void setGlobalData(GlobalData globalData) { this.globalData = globalData; }

    public Integer getForumLink() { return forumLink; }
    public void setForumLink(Integer forumLink) { this.forumLink = forumLink; }

    public Rankings getRanking() { return ranking; }
    public void setRanking(Rankings ranking) { this.ranking = ranking; }

    public Rankings getPreviousRanking() { return previousRanking; }
    public void setPreviousRanking(Rankings previousRanking) { this.previousRanking = previousRanking; }

    public boolean isPublicProfile() { return publicProfile; }
    public void setPublicProfile(boolean publicProfile) { this.publicProfile = publicProfile; }

    public Map<String, Character> getCharacters() { return characters; }
    public void setCharacters(Map<String, Character> characters) { this.characters = characters; }

    public Character getActiveCharacterObject() {
        if (activeCharacter != null && characters != null) {
            return characters.get(activeCharacter);
        }
        return null;
    }
}