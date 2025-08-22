package com.edgn.core.module.basic;

import com.edgn.core.minecraft.render.utils.ColorUtil;

public enum ModuleCategory {
    UTILITY("Utilities", "QoL I guess", 0x2ECC71),
    MOVEMENT("Movement", "Zoomin", ColorUtil.INSTANCE.LTBLUE),
    CHAT("Chat", "Messages and more", 0x3498DB),
    LOOTRUN("Lootrun", "Get these mythics", ColorUtil.INSTANCE.PURPLE),
    RENDER("Render", "Visuals", 0xF39C12),
    RAID("Raid", "Raid tracking and assistance", 0xE74C3C),
    FARMING("Farming", "Mob farming, proffing and more...", ColorUtil.INSTANCE.GREEN );


    private final String displayName;
    private final String description;
    private final int color;

    ModuleCategory(String displayName, String description, int color) {
        this.displayName = displayName;
        this.description = description;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getColor() { return color; }
}