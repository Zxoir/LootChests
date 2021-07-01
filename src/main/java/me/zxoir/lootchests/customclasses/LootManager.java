package me.zxoir.lootchests.customclasses;

import org.bukkit.World;

import java.util.concurrent.ConcurrentHashMap;

/**
 * MIT License Copyright (c) 2020 Zxoir
 *
 * @author Zxoir
 * @since 7/1/2021
 */
public class LootManager {
    private final ConcurrentHashMap<World, LootChest> cachedLootChests = new ConcurrentHashMap<>();
}
