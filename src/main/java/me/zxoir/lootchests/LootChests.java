package me.zxoir.lootchests;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * MIT License Copyright (c) 2020 Zxoir
 *
 * @author Zxoir
 * @since 7/1/2021
 */
public final class LootChests extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public enum LootChestType {
        NORMAL,
        RANDOM
    }
}
