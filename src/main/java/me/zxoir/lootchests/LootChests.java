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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

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
        //noinspection InstantiationOfUtilityClass
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
            if (block != null && block.getType().equals(Material.CHEST) && lootChest.getType().equals(LootChestType.RANDOM)) {
                ((Chest) block.getState()).getBlockInventory().clear();
                block.setType(AIR);
            }
        });

        for (Player player : LootChestManager.getEditLocations().keySet()) {
            LootChest lootChest = LootChestManager.getEditLocations().get(player);

            if (lootChest.getLocations().isEmpty()) continue;
            if (lootChest.getType().equals(LootChests.LootChestType.NORMAL)) {
                Optional<Location> firstKey = lootChest.getLocations().keySet().stream().findFirst();
                if (!firstKey.isPresent()) continue;
                Block block = firstKey.get().getBlock();
                BlockData blockData = block.getBlockData();
                if (blockData instanceof Directional) {
                    ((Directional) blockData).setFacing(lootChest.getLocations().get(firstKey.get()));
                    block.setBlockData(blockData);
                }
                Chest chest = (Chest) firstKey.get().getBlock();
                chest.getPersistentDataContainer().set(new NamespacedKey(LootChests.getInstance(), "LootChest"), PersistentDataType.INTEGER, lootChest.getId());
                chest.update();
                continue;
            }
            for (Location location : lootChest.getLocations().keySet()) {
                Block block = location.getBlock();
                if (!block.getType().equals(Material.CHEST)) continue;
                block.setType(Material.AIR);
            }
        }

    }


    public enum LootChestType {
        NORMAL,
        RANDOM
    }
}
