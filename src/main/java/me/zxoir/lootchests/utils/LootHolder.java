package me.zxoir.lootchests.utils;

import lombok.Getter;
import me.zxoir.lootchests.customclasses.LootChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/7/2021
 */
public class LootHolder implements InventoryHolder {
    @Getter
    LootChest lootChest;
    @Getter
    int chance;
    @Getter
    Player player;

    public LootHolder(Player player, LootChest lootChest, int chance) {
        this.player = player;
        this.lootChest = lootChest;
        this.chance = chance;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
