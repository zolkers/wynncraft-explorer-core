package com.edgn.core.wynncraft.item.enumerations;

public enum StatEnum {
    STRENGTHPOINTS("Strength", StatUnit.RAW, "rawStrength", "STRENGTH"),
    DEXTERITYPOINTS("Dexterity", StatUnit.RAW, "rawDexterity", "DEXTERITY"),
    INTELLIGENCEPOINTS("Intelligence", StatUnit.RAW, "rawIntelligence", "INTELLIGENCE"),
    DEFENSEPOINTS("Defence", StatUnit.RAW, "rawDefence", "DEFENSE"),
    AGILITYPOINTS("Agility", StatUnit.RAW, "rawAgility", "AGILITY"),
    DAMAGEBONUSRAW("Main Attack Damage", StatUnit.RAW, "rawMainAttackDamage", "DAMAGE_MAIN_ATTACK_ALL_RAW"),
    DAMAGEBONUS("Main Attack Damage", StatUnit.PERCENT, "mainAttackDamage", "DAMAGE_MAIN_ATTACK_ALL_PERCENT"),
    MAIN_ATTACK_NEUTRAL_DAMAGE_BONUS_RAW("Neutral Main Attack Damage", StatUnit.RAW, "rawNeutralMainAttackDamage", "DAMAGE_MAIN_ATTACK_NEUTRAL_RAW"),
    MAIN_ATTACK_NEUTRAL_DAMAGE_BONUS("Neutral Main Attack Damage", StatUnit.PERCENT, "neutralMainAttackDamage", "DAMAGE_MAIN_ATTACK_NEUTRAL_PERCENT"),
    MAIN_ATTACK_EARTH_DAMAGE_BONUS_RAW("Earth Main Attack Damage", StatUnit.RAW, "rawEarthMainAttackDamage", "DAMAGE_MAIN_ATTACK_EARTH_RAW"),
    MAIN_ATTACK_EARTH_DAMAGE_BONUS("Earth Main Attack Damage", StatUnit.PERCENT, "earthMainAttackDamage", "DAMAGE_MAIN_ATTACK_EARTH_PERCENT"),
    MAIN_ATTACK_THUNDER_DAMAGE_BONUS_RAW("Thunder Main Attack Damage", StatUnit.RAW, "rawThunderMainAttackDamage", "DAMAGE_MAIN_ATTACK_THUNDER_RAW"),
    MAIN_ATTACK_THUNDER_DAMAGE_BONUS("Thunder Main Attack Damage", StatUnit.PERCENT, "thunderMainAttackDamage", "DAMAGE_MAIN_ATTACK_THUNDER_PERCENT"),
    MAIN_ATTACK_WATER_DAMAGE_BONUS_RAW("Water Main Attack Damage", StatUnit.RAW, "rawWaterMainAttackDamage", "DAMAGE_MAIN_ATTACK_WATER_RAW"),
    MAIN_ATTACK_WATER_DAMAGE_BONUS("Water Main Attack Damage", StatUnit.PERCENT, "waterMainAttackDamage", "DAMAGE_MAIN_ATTACK_WATER_PERCENT"),
    MAIN_ATTACK_FIRE_DAMAGE_BONUS_RAW("Fire Main Attack Damage", StatUnit.RAW, "rawFireMainAttackDamage", "DAMAGE_MAIN_ATTACK_FIRE_RAW"),
    MAIN_ATTACK_FIRE_DAMAGE_BONUS("Fire Main Attack Damage", StatUnit.PERCENT, "fireMainAttackDamage", "DAMAGE_MAIN_ATTACK_FIRE_PERCENT"),
    MAIN_ATTACK_AIR_DAMAGE_BONUS_RAW("Air Main Attack Damage", StatUnit.RAW, "rawAirMainAttackDamage", "DAMAGE_MAIN_ATTACK_AIR_RAW"),
    MAIN_ATTACK_AIR_DAMAGE_BONUS("Air Main Attack Damage", StatUnit.PERCENT, "airMainAttackDamage", "DAMAGE_MAIN_ATTACK_AIR_PERCENT"),
    MAIN_ATTACK_ELEMENTAL_DAMAGE_BONUS_RAW("Elemental Main Attack Damage", StatUnit.RAW, "rawElementalMainAttackDamage", "DAMAGE_MAIN_ATTACK_RAINBOW_RAW"),
    MAIN_ATTACK_ELEMENTAL_DAMAGE_BONUS("Elemental Main Attack Damage", StatUnit.PERCENT, "elementalMainAttackDamage", "DAMAGE_MAIN_ATTACK_RAINBOW_PERCENT"),
    SPELLDAMAGERAW("Spell Damage", StatUnit.RAW, "rawSpellDamage", "DAMAGE_SPELL_ALL_RAW"),
    SPELLDAMAGE("Spell Damage", StatUnit.PERCENT, "spellDamage", "DAMAGE_SPELL_ALL_PERCENT"),
    SPELL_NEUTRAL_DAMAGE_BONUS_RAW("Neutral Spell Damage", StatUnit.RAW, "rawNeutralSpellDamage", "DAMAGE_SPELL_NEUTRAL_RAW"),
    SPELL_NEUTRAL_DAMAGE_BONUS("Neutral Spell Damage", StatUnit.PERCENT, "neutralSpellDamage", "DAMAGE_SPELL_NEUTRAL_PERCENT"),
    SPELL_EARTH_DAMAGE_BONUS_RAW("Earth Spell Damage", StatUnit.RAW, "rawEarthSpellDamage", "DAMAGE_SPELL_EARTH_RAW"),
    SPELL_EARTH_DAMAGE_BONUS("Earth Spell Damage", StatUnit.PERCENT, "earthSpellDamage", "DAMAGE_SPELL_EARTH_PERCENT"),
    SPELL_THUNDER_DAMAGE_BONUS_RAW("Thunder Spell Damage", StatUnit.RAW, "rawThunderSpellDamage", "DAMAGE_SPELL_THUNDER_RAW"),
    SPELL_THUNDER_DAMAGE_BONUS("Thunder Spell Damage", StatUnit.PERCENT, "thunderSpellDamage", "DAMAGE_SPELL_THUNDER_PERCENT"),
    SPELL_WATER_DAMAGE_BONUS_RAW("Water Spell Damage", StatUnit.RAW, "rawWaterSpellDamage", "DAMAGE_SPELL_WATER_RAW"),
    SPELL_WATER_DAMAGE_BONUS("Water Spell Damage", StatUnit.PERCENT, "waterSpellDamage", "DAMAGE_SPELL_WATER_PERCENT"),
    SPELL_FIRE_DAMAGE_BONUS_RAW("Fire Spell Damage", StatUnit.RAW, "rawFireSpellDamage", "DAMAGE_SPELL_FIRE_RAW"),
    SPELL_FIRE_DAMAGE_BONUS("Fire Spell Damage", StatUnit.PERCENT, "fireSpellDamage", "DAMAGE_SPELL_FIRE_PERCENT"),
    SPELL_AIR_DAMAGE_BONUS_RAW("Air Spell Damage", StatUnit.RAW, "rawAirSpellDamage", "DAMAGE_SPELL_AIR_RAW"),
    SPELL_AIR_DAMAGE_BONUS("Air Spell Damage", StatUnit.PERCENT, "airSpellDamage", "DAMAGE_SPELL_AIR_PERCENT"),
    RAINBOWSPELLDAMAGERAW("Elemental Spell Damage", StatUnit.RAW, "rawElementalSpellDamage", "RAINBOWSPELLDAMAGERAW"),
    SPELL_ELEMENTAL_DAMAGE_BONUS("Elemental Spell Damage", StatUnit.PERCENT, "elementalSpellDamage", "DAMAGE_SPELL_RAINBOW_PERCENT"),
    DAMAGE_BONUS_RAW("Damage", StatUnit.RAW, "rawDamage", "DAMAGE_ANY_ALL_RAW"),
    DAMAGE_BONUS("Damage", StatUnit.PERCENT, "damage", "DAMAGE_ANY_ALL_PERCENT"),
    NEUTRAL_DAMAGE_BONUS_RAW("Neutral Damage", StatUnit.RAW, "rawNeutralDamage", "DAMAGE_ANY_NEUTRAL_RAW"),
    NEUTRAL_DAMAGE_BONUS("Neutral Damage", StatUnit.PERCENT, "neutralDamage", "DAMAGE_ANY_NEUTRAL_PERCENT"),
    EARTH_DAMAGE_BONUS_RAW("Earth Damage", StatUnit.RAW, "rawEarthDamage", "DAMAGE_ANY_EARTH_RAW"),
    EARTHDAMAGEBONUS("Earth Damage", StatUnit.PERCENT, "earthDamage", "DAMAGE_ANY_EARTH_PERCENT"),
    THUNDER_DAMAGE_BONUS_RAW("Thunder Damage", StatUnit.RAW, "rawThunderDamage", "DAMAGE_ANY_THUNDER_RAW"),
    THUNDERDAMAGEBONUS("Thunder Damage", StatUnit.PERCENT, "thunderDamage", "DAMAGE_ANY_THUNDER_PERCENT"),
    WATER_DAMAGE_BONUS_RAW("Water Damage", StatUnit.RAW, "rawWaterDamage", "DAMAGE_ANY_WATER_RAW"),
    WATERDAMAGEBONUS("Water Damage", StatUnit.PERCENT, "waterDamage", "DAMAGE_ANY_WATER_PERCENT"),
    FIRE_DAMAGE_BONUS_RAW("Fire Damage", StatUnit.RAW, "rawFireDamage", "DAMAGE_ANY_FIRE_RAW"),
    FIREDAMAGEBONUS("Fire Damage", StatUnit.PERCENT, "fireDamage", "DAMAGE_ANY_FIRE_PERCENT"),
    AIR_DAMAGE_BONUS_RAW("Air Damage", StatUnit.RAW, "rawAirDamage", "DAMAGE_ANY_AIR_RAW"),
    AIRDAMAGEBONUS("Air Damage", StatUnit.PERCENT, "airDamage", "DAMAGE_ANY_AIR_PERCENT"),
    ELEMENTAL_DAMAGE_BONUS_RAW("Elemental Damage", StatUnit.RAW, "rawElementalDamage", "DAMAGE_ANY_RAINBOW_RAW"),
    ELEMENTAL_DAMAGE_BONUS("Elemental Damage", StatUnit.PERCENT, "elementalDamage", "DAMAGE_ANY_RAINBOW_PERCENT"),
    CRITICAL_DAMAGE_BONUS("Critical Damage Bonus", StatUnit.PERCENT, "criticalDamageBonus", "WEIRD_ASS_CRITICAL_DAMAGE_BONUS"),
    ELEMENTAL_DEFENSE("Elemental Defence", StatUnit.PERCENT, "elementalDefence", "ELEMENTAL_DEFENCE"),
    EARTHDEFENSE("Earth Defence", StatUnit.PERCENT, "earthDefence", "DEFENCE_EARTH"),
    THUNDERDEFENSE("Thunder Defence", StatUnit.PERCENT, "thunderDefence", "DEFENCE_THUNDER"),
    WATERDEFENSE("Water Defence", StatUnit.PERCENT, "waterDefence", "DEFENCE_WATER"),
    FIREDEFENSE("Fire Defence", StatUnit.PERCENT, "fireDefence", "DEFENCE_FIRE"),
    AIRDEFENSE("Air Defence", StatUnit.PERCENT, "airDefence", "DEFENCE_AIR"),
    SPELL_COST_RAW_1("1st Spell Cost", StatUnit.RAW, "raw1stSpellCost", "SPELL_FIRST_SPELL_COST_RAW"),
    SPELL_COST_PCT_1("1st Spell Cost", StatUnit.PERCENT, "1stSpellCost", "SPELL_FIRST_SPELL_COST_PERCENT"),
    SPELL_COST_RAW_2("2nd Spell Cost", StatUnit.RAW, "raw2ndSpellCost", "SPELL_SECOND_SPELL_COST_RAW"),
    SPELL_COST_PCT_2("2nd Spell Cost", StatUnit.PERCENT, "2ndSpellCost", "SPELL_SECOND_SPELL_COST_PERCENT"),
    SPELL_COST_RAW_3("3rd Spell Cost", StatUnit.RAW, "raw3rdSpellCost", "SPELL_THIRD_SPELL_COST_RAW"),
    SPELL_COST_PCT_3("3rd Spell Cost", StatUnit.PERCENT, "3rdSpellCost", "SPELL_THIRD_SPELL_COST_PERCENT"),
    SPELL_COST_RAW_4("4th Spell Cost", StatUnit.RAW, "raw4thSpellCost", "SPELL_FOURTH_SPELL_COST_RAW"),
    SPELL_COST_PCT_4("4th Spell Cost", StatUnit.PERCENT, "4thSpellCost", "SPELL_FOURTH_SPELL_COST_PERCENT"),
    HEALTHBONUS("Health", StatUnit.RAW, "rawHealth", "HEALTH"),
    HEALTHREGEN("Health Regen", StatUnit.PERCENT, "healthRegen", "HEALTH_REGEN_PERCENT"),
    HEALTHREGENRAW("Health Regen", StatUnit.RAW, "healthRegenRaw", "HEALTH_REGEN_RAW"),
    HEALING_EFFICIENCY("Healing Efficiency", StatUnit.PERCENT, "healingEfficiency", "HEALING_EFFICIENCY"),
    LIFESTEAL("Life Steal", StatUnit.PER_3_S, "lifeSteal", "LIFE_STEAL"),
    MANAREGEN("Mana Regen", StatUnit.PER_5_S, "manaRegen", "MANA_REGEN"),
    MANASTEAL("Mana Steal", StatUnit.PER_3_S, "manaSteal", "MANA_STEAL"),
    MAX_MANA("Max Mana", StatUnit.RAW, "rawMaxMana", "MAX_MANA_RAW"),
    SPEED("Walk Speed", StatUnit.PERCENT, "walkSpeed", "WALK_SPEED"),
    STAMINA("Sprint", StatUnit.PERCENT, "sprint", "SPRINT"),
    STAMINA_REGEN("Sprint Regen", StatUnit.PERCENT, "sprintRegen", "SPRINT_REGEN"),
    JUMP_HEIGHT("Jump Height", StatUnit.RAW, "jumpHeight", "JUMP_HEIGHT"),
    ATTACKSPEED("Attack Speed", StatUnit.TIER, "rawAttackSpeed", "ATTACK_SPEED"),
    MAIN_ATTACK_RANGE("Main Attack Range", StatUnit.PERCENT, "mainAttackRange", "MAIN_ATTACK_RANGE"),
    REFLECTION("Reflection", StatUnit.PERCENT, "reflection", "REFLECTION"),
    THORNS("Thorns", StatUnit.PERCENT, "thorns", "THORNS"),
    EXPLODING("Exploding", StatUnit.PERCENT, "exploding", "EXPLODING"),
    POISON("Poison", StatUnit.PER_3_S, "poison", "POISON"),
    KNOCKBACK("Knockback", StatUnit.PERCENT, "knockback", "KNOCKBACK"),
    SLOW_ENEMY("Slow Enemy", StatUnit.PERCENT, "slowEnemy", "SLOW_ENEMY"),
    WEAKEN_ENEMY("Weaken Enemy", StatUnit.PERCENT, "weakenEnemy", "WEAKEN_ENEMY"),
    EMERALDSTEALING("Stealing", StatUnit.PERCENT, "stealing", "STEALING"),
    XPBONUS("XP Bonus", StatUnit.PERCENT, "xpBonus", "XP_BONUS"),
    LOOTBONUS("Loot Bonus", StatUnit.PERCENT, "lootBonus", "LOOT_BONUS"),
    LOOTQUALITY("Loot Quality", StatUnit.PERCENT, "lootQuality", "LOOT_QUALITY"),
    GATHERXPBONUS("Gather XP Bonus", StatUnit.PERCENT, "gatherXpBonus", "GATHER_XP_BONUS"),
    GATHERSPEED("Gather Speed", StatUnit.PERCENT, "gatherSpeed", "GATHER_SPEED");

    private final String displayName;
    private final String apiName;
    private final StatUnit unit;
    private final String internalRollName;

    StatEnum(String displayName, StatUnit unit, String apiName, String internalRollName) {
        this.displayName = displayName;
        this.apiName = apiName;
        this.unit = unit;
        this.internalRollName = internalRollName;
    }

    public static StatEnum getStatFromApiName(String apiName) {
        for(StatEnum statEnum : values()) {
            if (statEnum.apiName.equals(apiName)) {
                return statEnum;
            }
        }
        return null;
    }
}