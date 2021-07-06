package me.zxoir.lootchests.listeners;

import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.events.AddLootChestEvent;
import me.zxoir.lootchests.events.RemoveLootChestEvent;
import me.zxoir.lootchests.managers.LootChestManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import static me.zxoir.lootchests.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/6/2021
 */
public class EditLocationListener implements Listener {

    @EventHandler
    public void onLootChestAdd(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!LootChestManager.getEditLocations().containsKey(player)) return;
        LootChest lootChest = LootChestManager.getEditLocations().get(player);

        if (!event.getBlockPlaced().getType().equals(Material.REDSTONE_BLOCK))
            return;

        AddLootChestEvent addLootChestEvent = new AddLootChestEvent(player, lootChest);
        Bukkit.getPluginManager().callEvent(addLootChestEvent);

        if (addLootChestEvent.isCancelled()) return;
        lootChest.addLocation(event.getBlockPlaced().getLocation());
    }

    @EventHandler
    public void onLootChestAdd(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!LootChestManager.getEditLocations().containsKey(player)) return;
        LootChest lootChest = LootChestManager.getEditLocations().get(player);

        if (!event.getBlock().getType().equals(Material.REDSTONE_BLOCK) || !lootChest.getLocations().contains(event.getBlock().getLocation()))
            return;

        RemoveLootChestEvent removeLootChestEvent = new RemoveLootChestEvent(player, lootChest);
        Bukkit.getPluginManager().callEvent(removeLootChestEvent);

        if (removeLootChestEvent.isCancelled()) return;
        lootChest.removeLocation(event.getBlock().getLocation());
    }

}
