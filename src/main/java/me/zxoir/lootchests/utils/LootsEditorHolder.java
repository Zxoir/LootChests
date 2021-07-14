package me.zxoir.lootchests.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zxoir.lootchests.customclasses.Loot;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.managers.LootChestManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.zxoir.lootchests.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/10/2021
 */
@SuppressWarnings("deprecation")
@AllArgsConstructor
public class LootsEditorHolder implements InventoryHolder {
    @Getter
    Loot loot;
    @Getter
    LootChest lootChest;
    @Getter
    boolean remove;
    @Getter
    private final static HashMap<UUID, Integer> pages = new HashMap<>();

    @Override
    public @NotNull Inventory getInventory() {
        if (loot != null) {
            if (remove) {
                Inventory inventory = Bukkit.createInventory(this, 27, colorize("&aEdit Loot"));
                ItemStack blueGlass = new ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).withName("&a").build();
                ItemStack confirm = new ItemStackBuilder(Material.GREEN_WOOL).withName("&AConfirm").withLore("&eYes! Delete this Loot.").build();
                ItemStack cancel = new ItemStackBuilder(Material.RED_WOOL).withName("&cCancel").withLore("&eNo! Cancel this request.").build();

                for (int i = 0; i <= 9; i++) {
                    inventory.setItem(i, blueGlass);
                }
                for (int i = 17; i < 27; i++) {
                    inventory.setItem(i, blueGlass);
                }
                inventory.setItem(12, confirm);
                inventory.setItem(14, cancel);

                return inventory;
            } else {
                Inventory inventory = Bukkit.createInventory(this, 27, colorize("&aEdit Loot"));
                inventory.setContents(loot.getItemStacks());
                return inventory;
            }

        } else if (lootChest != null) {
            Inventory inventory = Bukkit.createInventory(this, 9, colorize("&aEdit Loot"));

            if (lootChest.getLoots().size() > 9) {
                inventory = Bukkit.createInventory(this, 18, colorize("&aEdit Loot"));
                for (int i = 9; i < 18; i++) {
                    inventory.setItem(i, new ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).withName("&a").build());
                }

                List<Loot> subList = lootChest.getLoots().size() >= 9 ? lootChest.getLoots().subList(0, 9) : lootChest.getLoots().subList(0, lootChest.getLoots().size());
                for (int i = 0; i < subList.size(); i++) {
                    inventory.addItem(new ItemStackBuilder(Material.BOOK).withName("&aLoot " + i).withLore("&eLeft Click to edit this loot").withLore("&eRight Click to remove this Loot").build());
                }

                inventory.setItem(17, new ItemStackBuilder(Material.GREEN_DYE).withName("&aNext Page").withLore("&eClick to go to the next page").build());
            } else {
                for (int i = 0; i < lootChest.getLoots().size(); i++) {
                    inventory.addItem(new ItemStackBuilder(Material.BOOK).withName("&aLoot " + i).withLore("&eLeft Click to edit this loot").withLore("&eRight Click to remove this Loot").build());
                }
            }

            return inventory;
        } else {
            LinkedList<LootChest> lootChests = new LinkedList<>(LootChestManager.getLootChests().values());
            Inventory inventory = Bukkit.createInventory(this, 9, colorize("&aEdit Loot"));

            if (lootChests.size() > 9) {
                inventory = Bukkit.createInventory(this, 18, colorize("&aEdit Loot"));
                for (int i = 9; i < 18; i++) {
                    inventory.setItem(i, new ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).withName("&a").build());
                }

                List<LootChest> subList = lootChests.size() >= 9 ? lootChests.subList(0, 9) : lootChests.subList(0, lootChests.size());
                for (int i = 0; i < subList.size(); i++) {
                    LootChest lootChest = lootChests.get(i);
                    inventory.addItem(new ItemStackBuilder(Material.CHEST).resetFlags().withName("&7LootChest " + lootChest.getId()).withLore("&eClick here to edit the loot").build());
                }

                inventory.setItem(17, new ItemStackBuilder(Material.GREEN_DYE).withName("&aNext Page").withLore("&eClick to go to the next page").build());
            } else {
                for (LootChest lootChest : lootChests) {
                    inventory.addItem(new ItemStackBuilder(Material.CHEST).resetFlags().withName("&7LootChest " + lootChest.getId()).withLore("&eClick here to edit the loot").build());
                }
            }


            return inventory;
        }

    }

    public Inventory nextPage(UUID player) {
        if (!pages.containsKey(player)) {
            return getInventory();
        }
        int page = pages.get(player) + 1;
        pages.put(player, page);

        Inventory inventory = Bukkit.createInventory(this, 18, colorize("&aEdit Loot"));
        if (lootChest != null) {

            List<Loot> subList = lootChest.getLoots().size() >= page * 9 ? lootChest.getLoots().subList((page - 1) * 9, page * 9) : lootChest.getLoots().subList((page - 1) * 9, lootChest.getLoots().size());

            for (int i = 9; i < 18; i++) {
                inventory.setItem(i, new ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).withName("&a").build());
            }

            if (lootChest.getLoots().size() > page * 9) {
                inventory.setItem(17, new ItemStackBuilder(Material.GREEN_DYE).withName("&aNext Page").withLore("&eClick to go to the next page").build());
            }

            if (page > 1) {
                inventory.setItem(9, new ItemStackBuilder(Material.RED_DYE).withName("&cPrevious Page").withLore("&eClick to go to the previous page").build());
            }
            for (int i = 0; i < subList.size(); i++) {
                inventory.addItem(new ItemStackBuilder(Material.BOOK).withName("&aLoot " + (i + ((page - 1) * 9))).withLore("&eLeft Click to edit this loot").withLore("&eRight Click to remove this Loot").build());
            }

        } else {

            LinkedList<LootChest> lootChests = new LinkedList<>(LootChestManager.getLootChests().values());
            List<LootChest> subList = lootChests.size() >= page * 9 ? lootChests.subList((page - 1) * 9, page * 9) : lootChests.subList((page - 1) * 9, lootChests.size());

            for (int i = 9; i < 18; i++) {
                inventory.setItem(i, new ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).withName("&a").build());
            }

            if (lootChests.size() > page * 9) {
                inventory.setItem(17, new ItemStackBuilder(Material.GREEN_DYE).withName("&aNext Page").withLore("&eClick to go to the next page").build());
            }

            if (page > 1) {
                inventory.setItem(9, new ItemStackBuilder(Material.RED_DYE).withName("&cPrevious Page").withLore("&eClick to go to the previous page").build());
            }

            for (LootChest lootChest : subList) {
                inventory.addItem(new ItemStackBuilder(Material.CHEST).resetFlags().withName("&7LootChest " + lootChest.getId()).withLore("&eClick here to edit the loot").build());
            }

        }
        return inventory;
    }

    public Inventory previousPage(UUID player) {
        if (!pages.containsKey(player)) {
            return getInventory();
        }

        int page = pages.get(player) - 1;
        pages.put(player, page);

        Inventory inventory = Bukkit.createInventory(this, 18, colorize("&aEdit Loot"));
        if (lootChest != null) {
            List<Loot> subList = lootChest.getLoots().size() >= page * 9 ? lootChest.getLoots().subList((page - 1) * 9, page * 9) : lootChest.getLoots().subList((page - 1) * 9, lootChest.getLoots().size());

            for (int i = 9; i < 18; i++) {
                inventory.setItem(i, new ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).withName("&a").build());
            }

            if (lootChest.getLoots().size() > page * 9) {
                inventory.setItem(17, new ItemStackBuilder(Material.GREEN_DYE).withName("&aNext Page").withLore("&eClick to go to the next page").build());
            }

            if (page > 1) {
                inventory.setItem(9, new ItemStackBuilder(Material.RED_DYE).withName("&cPrevious Page").withLore("&eClick to go to the previous page").build());
            }

            for (int i = 0; i < subList.size(); i++) {
                inventory.addItem(new ItemStackBuilder(Material.BOOK).withName("&aLoot " + (i + ((page - 1) * 9))).withLore("&eLeft Click to edit this loot").withLore("&eRight Click to remove this Loot").build());
            }

        } else {
            LinkedList<LootChest> lootChests = new LinkedList<>(LootChestManager.getLootChests().values());
            List<LootChest> subList = lootChests.size() >= page * 9 ? lootChests.subList((page - 1) * 9, page * 9) : lootChests.subList((page - 1) * 9, lootChests.size());

            for (int i = 9; i < 18; i++) {
                inventory.setItem(i, new ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).withName("&a").build());
            }

            if (lootChests.size() > page * 9) {
                inventory.setItem(17, new ItemStackBuilder(Material.GREEN_DYE).withName("&aNext Page").withLore("&eClick to go to the next page").build());
            }

            if (page > 1) {
                inventory.setItem(9, new ItemStackBuilder(Material.RED_DYE).withName("&cPrevious Page").withLore("&eClick to go to the previous page").build());
            }

            for (LootChest lootChest : subList) {
                inventory.addItem(new ItemStackBuilder(Material.CHEST).resetFlags().withName("&7LootChest " + lootChest.getId()).withLore("&eClick here to edit the loot").build());
            }

        }
        return inventory;
    }

    private int getTotalItemStacks(ItemStack[] itemStacks) {
        int i = 0;
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR))
                i++;
        }

        return i;
    }

    private Integer getInventorySize(int size) {
        if (size <= 9)
            return 9;
        else if (size <= 18)
            return 18;
        else if (size <= 27)
            return 27;
        else if (size <= 36)
            return 36;
        else if (size <= 46)
            return 46;
        else if (size <= 54)
            return 54;
        else return null;
    }
}
