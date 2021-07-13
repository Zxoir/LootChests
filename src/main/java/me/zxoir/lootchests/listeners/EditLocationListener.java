package me.zxoir.lootchests.listeners;

import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.events.AddLootChestEvent;
import me.zxoir.lootchests.events.RemoveLootChestEvent;
import me.zxoir.lootchests.managers.LootChestManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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

        if (!event.getBlockPlaced().getType().equals(Material.CHEST))
            return;

        AddLootChestEvent addLootChestEvent = new AddLootChestEvent(player, lootChest);
        Bukkit.getPluginManager().callEvent(addLootChestEvent);

        if (addLootChestEvent.isCancelled()) return;
        if (lootChest.getType().equals(LootChests.LootChestType.NORMAL)) {
            if (lootChest.getLocations().isEmpty()) {
                BlockData blockData = event.getBlock().getBlockData();
                if (blockData instanceof Directional) {
                    lootChest.addLocation(event.getBlockPlaced().getLocation(), ((Directional) blockData).getFacing());
                }
                return;
            }

            player.sendMessage(colorize("&cYou can only have one location set for a Normal-Type LootChest."));
            event.setCancelled(true);
            return;
        }
        BlockData blockData = event.getBlock().getBlockData();
        if (blockData instanceof Directional) {
            lootChest.addLocation(event.getBlockPlaced().getLocation(), ((Directional) blockData).getFacing());
        }
    }

    @EventHandler
    public void onChestInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!LootChestManager.getEditLocations().containsKey(player)) return;
        LootChest lootChest = LootChestManager.getEditLocations().get(player);

        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.CHEST)) return;
        if (lootChest.getLocations().containsKey(event.getClickedBlock().getLocation()) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) event.setCancelled(true);
    }

    @EventHandler
    public void onLootChestRemove(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!event.getBlock().getType().equals(Material.CHEST)) return;

        if (!LootChestManager.getEditLocations().containsKey(player)) {
            Chest chest = (Chest) event.getBlock().getState();
            NamespacedKey namespacedKey = new NamespacedKey(LootChests.getInstance(), "LootChest");
            PersistentDataContainer container = chest.getPersistentDataContainer();
            if (container.has(namespacedKey, PersistentDataType.INTEGER)) {
                LootChest lootChest = LootChestManager.getLootChests().get(container.get(namespacedKey, PersistentDataType.INTEGER));
                if (LootChestManager.getEditLocations().containsValue(lootChest)) return;
                event.setCancelled(true);
            }
            return;
        }

        LootChest lootChest = LootChestManager.getEditLocations().get(player);

        if (!lootChest.getLocations().containsKey(event.getBlock().getLocation())) return;

        RemoveLootChestEvent removeLootChestEvent = new RemoveLootChestEvent(player, lootChest);
        Bukkit.getPluginManager().callEvent(removeLootChestEvent);

        if (removeLootChestEvent.isCancelled()) return;
        lootChest.removeLocation(event.getBlock().getLocation());
    }

}
