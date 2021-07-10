package me.zxoir.lootchests.listeners;

import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.customclasses.Loot;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.customclasses.SpawnTask;
import me.zxoir.lootchests.events.AddLootEvent;
import me.zxoir.lootchests.events.LootChestClaimEvent;
import me.zxoir.lootchests.utils.LootHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
 import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static me.zxoir.lootchests.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/7/2021
 */
public class LootListener implements Listener {

    @EventHandler
    public void onLootSave(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null || !event.getInventory().getHolder().getClass().equals(LootHolder.class)) return;
        LootHolder lootHolder = (LootHolder) event.getInventory().getHolder();
        LootChest lootChest = lootHolder.getLootChest();
        if (Arrays.stream(event.getInventory().getContents()).allMatch(itemStack -> itemStack == null || itemStack.getType().equals(Material.AIR))) return;

        AddLootEvent addLootEvent = new AddLootEvent(lootHolder.getPlayer(), event.getInventory().getContents(), lootHolder.getChance());
        Bukkit.getPluginManager().callEvent(addLootEvent);
        if (addLootEvent.isCancelled()) return;
        if (Arrays.stream(addLootEvent.getContents()).allMatch(itemStack -> itemStack == null || itemStack.getType().equals(Material.AIR))) return;
        lootChest.addLoot(new Loot(addLootEvent.getContents(), addLootEvent.getChance()));
        lootHolder.getPlayer().sendMessage(colorize("&aYou have added new loot to LootChest with ID " + lootChest.getId()));
    }

    @EventHandler
    public void onLoot(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || block == null || !block.getType().equals(Material.CHEST) || !SpawnTask.getChests().containsKey(block)) return;
        Chest chest = (Chest) block.getState();
        ItemStack[] contents = chest.getBlockInventory().getContents();
        LootChest lootChest = SpawnTask.getChests().get(block);
        LootChestClaimEvent lootChestClaimEvent = new LootChestClaimEvent(player, contents, block, lootChest);
        Bukkit.getPluginManager().callEvent(lootChestClaimEvent);

        if (lootChestClaimEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        player.openInventory(chest.getInventory());
        if (lootChest == null) return;
        lootChest.setClaimed(true);
        player.sendMessage(colorize("&aYou have claimed a LootChest!"));
        chest.getBlockInventory().clear();
        block.setType(Material.AIR);
        Bukkit.getScheduler().runTaskLater(LootChests.getInstance(), () -> player.getOpenInventory().getTopInventory().setContents(lootChestClaimEvent.getContents()), 1);
    }
}
