package me.zxoir.lootchests;

import lombok.Getter;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.utils.LootChestsDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2020 Zxoir
 *
 * @author Zxoir
 * @since 7/1/2021
 */
public final class LootChests extends JavaPlugin {
    @Getter
    private static LootChests instance;
    @Getter
    private static final Logger logger = LogManager.getLogger("LootChests");

    @Override
    public void onEnable() {
        instance = this;

        logger.info("======================================================================");
        long initalTime = System.currentTimeMillis();

        long start = System.currentTimeMillis();
        new LootChestsDB("CREATE TABLE IF NOT EXISTS Shop(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "lootchestData text NOT NULL" +
                ");");
        logger.info("Completed setting up the database in " + (System.currentTimeMillis() - start) + "ms");

        logger.info("Completed plugin setup in " + (System.currentTimeMillis() - initalTime) + "ms");
        logger.info("======================================================================");
    }

    public enum LootChestType {
        NORMAL,
        RANDOM
    }
}
