package me.zxoir.lootchests;

import lombok.Getter;
import me.zxoir.lootchests.commands.MainCommand;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.listeners.EditLocationListener;
import me.zxoir.lootchests.listeners.LootListener;
import me.zxoir.lootchests.managers.ConfigManager;
import me.zxoir.lootchests.managers.LootChestManager;
import me.zxoir.lootchests.utils.LootChestsDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Material.AIR;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/1/2021
 */
public final class LootChests extends JavaPlugin {
    @Getter
    private static LootChests instance;
    @Getter
    private static final Logger lcLogger = LogManager.getLogger("LootChests");

    @Override
    public void onEnable() {
        instance = this;

        lcLogger.info("======================================================================");
        long initalTime = System.currentTimeMillis();

        long start = System.currentTimeMillis();
        saveDefaultConfig();
        new LootChestsDB("CREATE TABLE IF NOT EXISTS LootChests(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "lootchestData text NOT NULL" +
                ");");
        lcLogger.info("Completed setting up the database in " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        LootChestManager.setup();
        ConfigManager.setup();
        lcLogger.info("Loaded managers in " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        getCommand("lootchests").setExecutor(new MainCommand());
        getServer().getPluginManager().registerEvents(new EditLocationListener(), this);
        getServer().getPluginManager().registerEvents(new LootListener(), this);
        lcLogger.info("Loaded commands and listeners in " + (System.currentTimeMillis() - start) + "ms");

        lcLogger.info("Completed plugin setup in " + (System.currentTimeMillis() - initalTime) + "ms");
        lcLogger.info("======================================================================");
    }

    @Override
    public void onDisable() {
        LootChestManager.getLootChests().values().forEach(lootChest -> {
            Block block = lootChest.getSpawnTask().getLastSpawned();
            if (block != null && block.getType().equals(Material.CHEST)) {
                ((Chest) block.getState()).getBlockInventory().clear();
                block.setType(AIR);
            }
        });
    }

    public enum LootChestType {
        NORMAL,
        RANDOM
    }
}
