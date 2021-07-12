package me.zxoir.lootchests.listeners;

import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.customclasses.Loot;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.customclasses.SpawnTask;
import me.zxoir.lootchests.events.AddLootEvent;
import me.zxoir.lootchests.events.LootChestClaimEvent;
import me.zxoir.lootchests.events.ModifyLootEvent;
import me.zxoir.lootchests.events.RemoveLootEvent;
import me.zxoir.lootchests.managers.LootChestManager;
import me.zxoir.lootchests.utils.LootHolder;
import me.zxoir.lootchests.utils.LootsEditorHolder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static me.zxoir.lootchests.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/7/2021
 */
@SuppressWarnings("deprecation")
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

    @EventHandler
    public void onLootModify(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null || !event.getInventory().getHolder().getClass().equals(LootsEditorHolder.class)) return;
        LootsEditorHolder holder = (LootsEditorHolder) event.getInventory().getHolder();
        if (holder.getLootChest() == null || holder.getLoot() == null || holder.isRemove()) return;

        if (Arrays.stream(event.getInventory().getContents()).allMatch(itemStack -> itemStack == null || itemStack.getType().equals(Material.AIR))) return;

        ModifyLootEvent modifyLootEvent = new ModifyLootEvent((Player) event.getPlayer(), holder.getLoot());
        ItemStack[] oldLoot = holder.getLoot().getItemStacks();
        Bukkit.getPluginManager().callEvent(modifyLootEvent);
        if (modifyLootEvent.isCancelled()) return;
        if (Arrays.stream(event.getInventory().getContents()).allMatch(itemStack -> itemStack == null || itemStack.getType().equals(Material.AIR))) return;
        holder.getLoot().setItemStacks(event.getInventory().getContents());
        if (!Arrays.equals(oldLoot, event.getInventory().getContents()))
            event.getPlayer().sendMessage(colorize("&aSuccessfully modified Loot!"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null || !event.getInventory().getHolder().getClass().equals(LootsEditorHolder.class) || event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;
        Player player = (Player) event.getWhoClicked();
        LootsEditorHolder holder = (LootsEditorHolder) event.getInventory().getHolder();
        event.setCancelled(true);

        if (holder.getLoot() != null) {
            if (holder.isRemove()) {
                if (event.getCurrentItem().getType().equals(Material.RED_WOOL)) {
                    Inventory inventory = new LootsEditorHolder(null, holder.getLootChest(), false).getInventory();
                    player.openInventory(inventory);
                } else if (event.getCurrentItem().getType().equals(Material.GREEN_WOOL)) {
                    RemoveLootEvent removeLootEvent = new RemoveLootEvent(player, holder.getLoot(), holder.getLootChest());
                    Bukkit.getPluginManager().callEvent(removeLootEvent);

                    if (removeLootEvent.isCancelled()) return;

                    player.closeInventory();
                    player.sendMessage(colorize("&aLoot successfully removed."));
                    holder.getLootChest().getLoots().remove(holder.getLoot());
                }
                return;
            }
            event.setCancelled(false);
        } else if (holder.getLootChest() != null) {
            if (!event.getCurrentItem().getType().equals(Material.BOOK)) return;
            int id = Integer.parseInt(StringUtils.substringAfter(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()), " "));
            Loot loot = holder.getLootChest().getLoots().get(id);

            if (loot == null) {
                player.sendMessage(colorize("&cLoot not found."));
                player.closeInventory();
                return;
            }

            if (event.getClick().equals(ClickType.RIGHT)) {
                Inventory inventory = new LootsEditorHolder(loot, holder.getLootChest(), true).getInventory();
                player.openInventory(inventory);
                return;
            }

            Inventory inventory = new LootsEditorHolder(loot, holder.getLootChest(), false).getInventory();
            player.openInventory(inventory);
        } else {
            if (!event.getCurrentItem().getType().equals(Material.CHEST)) return;
            int id = Integer.parseInt(StringUtils.substringAfter(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()), " "));
            LootChest lootChest = LootChestManager.getLootChests().get(id);

            if (lootChest == null) {
                player.sendMessage(colorize("&cLootChest not found."));
                player.closeInventory();
                return;
            }

            if (lootChest.getLoots().isEmpty()) {
                player.sendMessage(colorize("&cThis LootChest does not contain any Loot."));
                player.closeInventory();
                return;
            }

            Inventory inventory = new LootsEditorHolder(null, lootChest, false).getInventory();
            player.openInventory(inventory);
        }

    }
}
