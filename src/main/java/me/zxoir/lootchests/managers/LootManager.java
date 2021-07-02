package me.zxoir.lootchests.managers;

import lombok.Getter;
import me.zxoir.lootchests.customclasses.LootChest;
import org.bukkit.World;

import java.util.concurrent.ConcurrentHashMap;

/**
 * MIT License Copyright (c) 2020 Zxoir
 *
 * @author Zxoir
 * @since 7/1/2021
 */
public class LootManager {
    @Getter
    private final ConcurrentHashMap<Integer, LootChest> cachedLootChests = new ConcurrentHashMap<>();
}
