/*
 * Copyright © Wynntils 2023-2024.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.edgn.core.minecraft.render.utils;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.UUID;

public final class SkinUtil {
    private static void setPlayerHeadFromUUID(ItemStack itemStack, String uuid) {
        JsonObject skinObject = new JsonObject();
        skinObject.addProperty("url", "https://textures.minecraft.net/texture/" + uuid);

        JsonObject texturesObject = new JsonObject();
        texturesObject.add("SKIN", skinObject);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("textures", texturesObject);

        String textureString =
                Base64.getEncoder().encodeToString(jsonObject.toString().getBytes(Charset.defaultCharset()));

        setPlayerHeadSkin(itemStack, textureString);
    }

    private static void setPlayerHeadSkin(ItemStack itemStack, String textureString) {
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", textureString, null));

        itemStack.set(DataComponentTypes.PROFILE, new ProfileComponent(gameProfile));
    }

    public static ItemStack fromPlayerHeadUUID(String uuid) {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD, 1);
        setPlayerHeadFromUUID(itemStack, uuid);
        return itemStack;
    }

}
