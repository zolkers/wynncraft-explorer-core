package com.edgn.core.wynncraft.player.parsers;

import com.edgn.Main;
import com.edgn.core.wynncraft.player.WynncraftPlayer;
import com.edgn.core.wynncraft.player.statistics.*;
import com.edgn.core.wynncraft.player.statistics.Character;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Parser for Wynncraft Player data
 */
public class WynncraftPlayerParser {
    private static final Gson gson = new Gson();

    /**
     * Parse a player from JSON string
     * @param jsonData The JSON data string
     * @return A WynncraftPlayer object
     */
    public static WynncraftPlayer parsePlayer(String jsonData) {
        try {
            JsonObject playerJson = gson.fromJson(jsonData, JsonObject.class);
            return parsePlayerFromJson(playerJson);
        } catch (Exception e) {
            Main.OVERLAY_MANAGER.getLoggerOverlay().error(e.getMessage(), true);
            return null;
        }
    }

    /**
     * Parse a player from JsonObject
     * @param playerJson The JsonObject containing player data
     * @return A WynncraftPlayer object
     */
    private static WynncraftPlayer parsePlayerFromJson(JsonObject playerJson) {
        WynncraftPlayer player = new WynncraftPlayer();

        Main.OVERLAY_MANAGER.getLoggerOverlay().action("Parsing player informations", true);

        player.setUsername(safeGetString(playerJson, "username"));
        player.setOnline(safeGetBoolean(playerJson, "online"));
        player.setServer(safeGetString(playerJson, "server"));
        player.setActiveCharacter(safeGetString(playerJson, "activeCharacter"));
        player.setUuid(safeGetString(playerJson, "uuid"));
        player.setRank(safeGetString(playerJson, "rank"));
        player.setRankBadge(safeGetString(playerJson, "rankBadge"));
        player.setShortenedRank(safeGetString(playerJson, "shortenedRank"));
        player.setSupportRank(safeGetString(playerJson, "supportRank"));


        if (playerJson.has("veteran") && !playerJson.get("veteran").isJsonNull()) {
            player.setVeteran(playerJson.get("veteran").getAsBoolean());
        }

        player.setFirstJoin(safeGetString(playerJson, "firstJoin"));
        player.setLastJoin(safeGetString(playerJson, "lastJoin"));

        if (playerJson.has("playtime")) {
            player.setPlaytime(playerJson.get("playtime").getAsDouble());
        }

        player.setPublicProfile(safeGetBoolean(playerJson, "publicProfile"));

        if (playerJson.has("forumLink") && !playerJson.get("forumLink").isJsonNull()) {
            player.setForumLink(playerJson.get("forumLink").getAsInt());
        }


        if (playerJson.has("legacyRankColour") && !playerJson.get("legacyRankColour").isJsonNull()) {
            JsonObject colorJson = playerJson.getAsJsonObject("legacyRankColour");
            RankColor rankColor = new RankColor();
            rankColor.setMain(safeGetString(colorJson, "main"));
            rankColor.setSub(safeGetString(colorJson, "sub"));
            player.setLegacyRankColour(rankColor);
        }

        if (playerJson.has("guild") && !playerJson.get("guild").isJsonNull()) {
            JsonObject guildJson = playerJson.getAsJsonObject("guild");
            Guild guild = new Guild();
            guild.setUuid(safeGetString(guildJson, "uuid"));
            guild.setName(safeGetString(guildJson, "name"));
            guild.setPrefix(safeGetString(guildJson, "prefix"));
            guild.setRank(safeGetString(guildJson, "rank"));
            guild.setRankStars(safeGetString(guildJson, "rankStars"));
            player.setGuild(guild);
        }

        if (playerJson.has("globalData") && !playerJson.get("globalData").isJsonNull()) {
            JsonObject globalDataJson = playerJson.getAsJsonObject("globalData");
            GlobalData globalData = new GlobalData();

            if (globalDataJson.has("wars")) {
                globalData.setWars(globalDataJson.get("wars").getAsInt());
            }

            if (globalDataJson.has("totalLevel")) {
                globalData.setTotalLevel(globalDataJson.get("totalLevel").getAsInt());
            }

            if (globalDataJson.has("killedMobs")) {
                globalData.setKilledMobs(globalDataJson.get("killedMobs").getAsInt());
            }

            if (globalDataJson.has("chestsFound")) {
                globalData.setChestsFound(globalDataJson.get("chestsFound").getAsInt());
            }

            if (globalDataJson.has("completedQuests")) {
                globalData.setCompletedQuests(globalDataJson.get("completedQuests").getAsInt());
            }

            if (globalDataJson.has("dungeons") && !globalDataJson.get("dungeons").isJsonNull()) {
                JsonObject dungeonsJson = globalDataJson.getAsJsonObject("dungeons");
                GameStats dungeons = new GameStats();

                if (dungeonsJson.has("total")) {
                    dungeons.setTotal(dungeonsJson.get("total").getAsInt());
                }

                if (dungeonsJson.has("list") && !dungeonsJson.get("list").isJsonNull()) {
                    Map<String, Integer> dungeonsList = new HashMap<>();
                    JsonObject listJson = dungeonsJson.getAsJsonObject("list");

                    for (Entry<String, JsonElement> entry : listJson.entrySet()) {
                        dungeonsList.put(entry.getKey(), entry.getValue().getAsInt());
                    }

                    dungeons.setList(dungeonsList);
                }

                globalData.setDungeons(dungeons);
            }

            if (globalDataJson.has("raids") && !globalDataJson.get("raids").isJsonNull()) {
                JsonObject raidsJson = globalDataJson.getAsJsonObject("raids");
                GameStats raids = new GameStats();

                if (raidsJson.has("total")) {
                    raids.setTotal(raidsJson.get("total").getAsInt());
                }

                if (raidsJson.has("list") && !raidsJson.get("list").isJsonNull()) {
                    Map<String, Integer> raidsList = new HashMap<>();
                    JsonObject listJson = raidsJson.getAsJsonObject("list");

                    for (Entry<String, JsonElement> entry : listJson.entrySet()) {
                        raidsList.put(entry.getKey(), entry.getValue().getAsInt());
                    }

                    raids.setList(raidsList);
                }

                globalData.setRaids(raids);
            }

            if (globalDataJson.has("pvp") && !globalDataJson.get("pvp").isJsonNull()) {
                JsonObject pvpJson = globalDataJson.getAsJsonObject("pvp");
                PvpStats pvp = new PvpStats();

                if (pvpJson.has("kills")) {
                    pvp.setKills(pvpJson.get("kills").getAsInt());
                }

                if (pvpJson.has("deaths")) {
                    pvp.setDeaths(pvpJson.get("deaths").getAsInt());
                }

                globalData.setPvp(pvp);
            }

            player.setGlobalData(globalData);
        }


        if (playerJson.has("ranking") && !playerJson.get("ranking").isJsonNull()) {
            Rankings rankings = new Rankings();
            JsonObject rankingJson = playerJson.getAsJsonObject("ranking");

            for (Entry<String, JsonElement> entry : rankingJson.entrySet()) {
                rankings.put(entry.getKey(), entry.getValue().getAsInt());
            }

            player.setRanking(rankings);
        }


        if (playerJson.has("previousRanking") && !playerJson.get("previousRanking").isJsonNull()) {
            Rankings previousRankings = new Rankings();
            JsonObject previousRankingJson = playerJson.getAsJsonObject("previousRanking");

            for (Entry<String, JsonElement> entry : previousRankingJson.entrySet()) {
                previousRankings.put(entry.getKey(), entry.getValue().getAsInt());
            }

            player.setPreviousRanking(previousRankings);
        }

        Main.OVERLAY_MANAGER.getLoggerOverlay().info("Preparing to retrieve player's characters", true);

        if (playerJson.has("characters") && !playerJson.get("characters").isJsonNull()) {
            Map<String, Character> characters = new HashMap<>();
            JsonObject charactersJson = playerJson.getAsJsonObject("characters");

            for (Entry<String, JsonElement> entry : charactersJson.entrySet()) {
                String characterId = entry.getKey();
                JsonObject characterJson = entry.getValue().getAsJsonObject();
                Character character = parseCharacter(characterJson);
                characters.put(characterId, character);
            }

            player.setCharacters(characters);
        }

        Main.OVERLAY_MANAGER.getLoggerOverlay().success("Characters successfully retrieved", true);

        return player;
    }

    /**
     * Parse a character from JsonObject
     * @param characterJson The JsonObject containing character data
     * @return A Character object
     */
    private static Character parseCharacter(JsonObject characterJson) {
        Character character = new Character();

        character.setType(safeGetString(characterJson, "type"));
        character.setReskin(safeGetString(characterJson, "reskin"));
        character.setNickname(safeGetString(characterJson, "nickname"));

        if (characterJson.has("level") && !characterJson.get("level").isJsonNull()) {
            character.setLevel(characterJson.get("level").getAsInt());
        }

        if (characterJson.has("xp") && !characterJson.get("xp").isJsonNull()) {
            character.setXp(characterJson.get("xp").getAsLong());
        }

        if (characterJson.has("xpPercent") && !characterJson.get("xpPercent").isJsonNull()) {
            character.setXpPercent(characterJson.get("xpPercent").getAsInt());
        }

        if (characterJson.has("totalLevel") && !characterJson.get("totalLevel").isJsonNull()) {
            character.setTotalLevel(characterJson.get("totalLevel").getAsInt());
        }

        if (characterJson.has("wars") && !characterJson.get("wars").isJsonNull()) {
            character.setWars(characterJson.get("wars").getAsInt());
        }

        if (characterJson.has("playtime") && !characterJson.get("playtime").isJsonNull()) {
            character.setPlaytime(characterJson.get("playtime").getAsDouble());
        }

        if (characterJson.has("mobsKilled") && !characterJson.get("mobsKilled").isJsonNull()) {
            try {
                character.setMobsKilled(characterJson.get("mobsKilled").getAsInt());
            } catch (Exception e) {
                Main.OVERLAY_MANAGER.getLoggerOverlay().warn("Couldn't retrieve Mob Killed number", true);
                character.setChestsFound(-1);
            }
        }

        if (characterJson.has("chestsFound") && !characterJson.get("chestsFound").isJsonNull()) {
            try {
                character.setChestsFound(characterJson.get("chestsFound").getAsInt());
            } catch (Exception e) {
                Main.OVERLAY_MANAGER.getLoggerOverlay().warn("Couldn't retrieve Chests found number", true);
                character.setChestsFound(-1);
            }
        }

        if (characterJson.has("itemsIdentified") && !characterJson.get("itemsIdentified").isJsonNull()) {
            try {
                character.setItemsIdentified(characterJson.get("itemsIdentified").getAsInt());
            } catch (Exception e) {
                Main.OVERLAY_MANAGER.getLoggerOverlay().warn("Couldn't retrieve Items identified number", true);
                character.setItemsIdentified(-1);
            }
        }

        if (characterJson.has("blocksWalked") && !characterJson.get("blocksWalked").isJsonNull()) {
            character.setBlocksWalked(characterJson.get("blocksWalked").getAsLong());
        }

        if (characterJson.has("logins") && !characterJson.get("logins").isJsonNull()) {
            character.setLogins(characterJson.get("logins").getAsInt());
        }

        if (characterJson.has("deaths") && !characterJson.get("deaths").isJsonNull()) {
            try {
                character.setDeaths(characterJson.get("deaths").getAsInt());
            } catch (Exception e) {
                Main.OVERLAY_MANAGER.getLoggerOverlay().warn("Couldn't retrieve Character death number", true);
                character.setDeaths(-1);
            }
        }

        if (characterJson.has("discoveries") && !characterJson.get("discoveries").isJsonNull()) {
            try {
                character.setDiscoveries(characterJson.get("discoveries").getAsInt());
            } catch (Exception e) {
                Main.OVERLAY_MANAGER.getLoggerOverlay().warn("Couldn't retrieve Discoveries number", true);
                character.setDiscoveries(-1);
            }
        }

        if (characterJson.has("preEconomy") && !characterJson.get("preEconomy").isJsonNull()) {
            character.setPreEconomy(characterJson.get("preEconomy"));
        }

        if (characterJson.has("pvp") && !characterJson.get("pvp").isJsonNull()) {
            JsonObject pvpJson = characterJson.getAsJsonObject("pvp");
            PvpStats pvp = new PvpStats();

            if (pvpJson.has("kills") && !pvpJson.get("kills").isJsonNull()) {
                try {
                    pvp.setKills(pvpJson.get("kills").getAsInt());
                } catch (Exception e) {
                    Main.OVERLAY_MANAGER.getLoggerOverlay().warn("Couldn't retrieve Kills number", true);
                    pvp.setKills(-1);
                }
            }

            if (pvpJson.has("deaths") && !pvpJson.get("deaths").isJsonNull()) {
                try {
                    pvp.setDeaths(pvpJson.get("deaths").getAsInt());
                } catch (Exception e) {
                    Main.OVERLAY_MANAGER.getLoggerOverlay().warn("Couldn't retrieve PVP Deaths number", true);
                    pvp.setDeaths(-1);
                }
            }

            character.setPvp(pvp);
        } else {
            Main.OVERLAY_MANAGER.getLoggerOverlay().warn("Couldn't retrieve PvPStats", true);
            character.setPvp(new PvpStats());
        }

        if (characterJson.has("gamemode") && !characterJson.get("gamemode").isJsonNull()) {
            List<String> gamemode = new ArrayList<>();
            JsonArray gamemodeArray = characterJson.getAsJsonArray("gamemode");
            for (JsonElement element : gamemodeArray) {
                if (!element.isJsonNull()) {
                    gamemode.add(element.getAsString());
                }
            }
            character.setGamemode(gamemode);
        } else {
            character.setGamemode(new ArrayList<>());
        }

        if (characterJson.has("skillPoints") && !characterJson.get("skillPoints").isJsonNull()) {
            Map<String, Integer> skillPoints = new HashMap<>();
            JsonObject skillPointsJson = characterJson.getAsJsonObject("skillPoints");

            for (Entry<String, JsonElement> entry : skillPointsJson.entrySet()) {
                if (!entry.getValue().isJsonNull()) {
                    skillPoints.put(entry.getKey(), entry.getValue().getAsInt());
                }
            }

            character.setSkillPoints(skillPoints);
        } else {
            character.setSkillPoints(new HashMap<>());
        }

        if (characterJson.has("professions") && !characterJson.get("professions").isJsonNull()) {
            Map<String, ProfessionLevel> professions = new HashMap<>();
            JsonObject professionsJson = characterJson.getAsJsonObject("professions");

            for (Entry<String, JsonElement> entry : professionsJson.entrySet()) {
                String professionName = entry.getKey();
                if (!entry.getValue().isJsonNull()) {
                    ProfessionLevel professionLevel = getProfessionLevel(entry);

                    professions.put(professionName, professionLevel);
                }
            }

            character.setProfessions(professions);
        } else {
            character.setProfessions(new HashMap<>());
        }

        if (characterJson.has("dungeons") && !characterJson.get("dungeons").isJsonNull()) {
            JsonObject dungeonsJson = characterJson.getAsJsonObject("dungeons");
            GameStats dungeons = new GameStats();

            if (dungeonsJson.has("total") && !dungeonsJson.get("total").isJsonNull()) {
                dungeons.setTotal(dungeonsJson.get("total").getAsInt());
            }

            if (dungeonsJson.has("list") && !dungeonsJson.get("list").isJsonNull()) {
                Map<String, Integer> dungeonsList = new HashMap<>();
                JsonObject listJson = dungeonsJson.getAsJsonObject("list");

                for (Entry<String, JsonElement> entry : listJson.entrySet()) {
                    if (!entry.getValue().isJsonNull()) {
                        dungeonsList.put(entry.getKey(), entry.getValue().getAsInt());
                    }
                }

                dungeons.setList(dungeonsList);
            } else {
                dungeons.setList(new HashMap<>());
            }

            character.setDungeons(dungeons);
        } else {
            GameStats emptyDungeons = new GameStats();
            emptyDungeons.setList(new HashMap<>());
            character.setDungeons(emptyDungeons);
        }

        if (characterJson.has("raids") && !characterJson.get("raids").isJsonNull()) {
            JsonObject raidsJson = characterJson.getAsJsonObject("raids");
            GameStats raids = new GameStats();

            if (raidsJson.has("total") && !raidsJson.get("total").isJsonNull()) {
                raids.setTotal(raidsJson.get("total").getAsInt());
            }

            if (raidsJson.has("list") && !raidsJson.get("list").isJsonNull()) {
                Map<String, Integer> raidsList = new HashMap<>();
                JsonObject listJson = raidsJson.getAsJsonObject("list");

                for (Entry<String, JsonElement> entry : listJson.entrySet()) {
                    if (!entry.getValue().isJsonNull()) {
                        raidsList.put(entry.getKey(), entry.getValue().getAsInt());
                    }
                }

                raids.setList(raidsList);
            } else {
                raids.setList(new HashMap<>());
            }

            character.setRaids(raids);
        } else {
            GameStats emptyRaids = new GameStats();
            emptyRaids.setList(new HashMap<>());
            character.setRaids(emptyRaids);
        }

        if (characterJson.has("quests") && !characterJson.get("quests").isJsonNull()) {
            List<String> quests = new ArrayList<>();
            JsonArray questsArray = characterJson.getAsJsonArray("quests");
            for (JsonElement element : questsArray) {
                if (!element.isJsonNull()) {
                    quests.add(element.getAsString());
                }
            }
            character.setQuests(quests);
        } else {
            character.setQuests(new ArrayList<>());
        }

        return character;
    }

    private static @NotNull ProfessionLevel getProfessionLevel(Entry<String, JsonElement> entry) {
        JsonObject professionJson = entry.getValue().getAsJsonObject();

        ProfessionLevel professionLevel = new ProfessionLevel();
        if (professionJson.has("level") && !professionJson.get("level").isJsonNull()) {
            professionLevel.setLevel(professionJson.get("level").getAsInt());
        }

        if (professionJson.has("xpPercent") && !professionJson.get("xpPercent").isJsonNull()) {
            professionLevel.setXpPercent(professionJson.get("xpPercent").getAsInt());
        }
        return professionLevel;
    }

    /**
     * Safely gets a string from a JSON object
     * @param json The JSON object
     * @param key The key to get
     * @return The string value, or null if not found or null
     */
    private static String safeGetString(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return null;
    }

    /**
     * Safely gets a boolean from a JSON object
     * @param json The JSON object
     * @param key The key to get
     * @return The boolean value, or false if not found or null
     */
    private static boolean safeGetBoolean(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsBoolean();
        }
        return false;
    }
}