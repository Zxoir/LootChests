package me.zxoir.lootchests.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.lootchests.LootChests;
import org.bukkit.Location;

import java.util.LinkedList;

/**
 * MIT License Copyright (c) 2020 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
@AllArgsConstructor
@Setter
@Getter
public class LootChestDB {
    Long interval;
    LootChests.LootChestType type;
    LinkedList<Loot> loots;
    LinkedList<Location> locations;
    int lootAmount;
    int totalWeight;
    int id;
}
