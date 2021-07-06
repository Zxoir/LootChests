package me.zxoir.lootchests.managers;

import lombok.Getter;
import me.zxoir.lootchests.customclasses.LootChest;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
public class LootChestManager {
    @Getter
    private static final HashMap<Integer, LootChest> lootChests = new HashMap<>();
    private static boolean setup = false;
    private static int ID = -1;

    public static void setup() {
        if (setup) return;
        ID = LootChestsDBManager.getAutoIncrementValue();
        LootChestsDBManager.getLootChests().forEach(lootChest -> lootChests.put(lootChest.getId(), lootChest));

        setup = true;
    }

    public static boolean registerLootChest(@NotNull LootChest lootChest) {
        if (lootChests.containsKey(lootChest.getId())) return false;
        LootChestsDBManager.saveLootChestToDB(lootChest.getSerializedLootChest());
        return true;
    }

    public static int getID() {
        return ID;
    }

    public static int getNewID() {
        return ++ID;
    }
}
