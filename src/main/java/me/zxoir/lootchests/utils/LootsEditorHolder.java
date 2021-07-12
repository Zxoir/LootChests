package me.zxoir.lootchests.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zxoir.lootchests.customclasses.Loot;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.managers.LootChestManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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

    @Override
    public @NotNull Inventory getInventory() { // todo: pages
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
            Inventory inventory = Bukkit.createInventory(this, 27, colorize("&aEdit Loot"));
            for (int i = 0; i < lootChest.getLoots().size(); i++) {
                inventory.addItem(new ItemStackBuilder(Material.BOOK).withName("&aLoot " + i).withLore("&eLeft Click to edit this loot").withLore("&eRight Click to remove this Loot").build());
                i++;
            }

            return inventory;
        } else {
            int totalSize = 0;
            Collection<LootChest> lootChests = LootChestManager.getLootChests().values();
            for (LootChest chest : lootChests) {
                for (Loot loot : chest.getLoots()) {
                    totalSize += getTotalItemStacks(loot.getItemStacks());
                }
            }
            Integer size = getInventorySize(totalSize);
            Inventory inventory = Bukkit.createInventory(this, 9, colorize("&aEdit Loot"));
            if (size != null) // Todo: make multiple pages
                inventory = Bukkit.createInventory(this, size, colorize("&aEdit Loot"));

            for (LootChest lootChest : lootChests) {
                inventory.addItem(new ItemStackBuilder(Material.CHEST).resetFlags().withName("&7LootChest " + lootChest.getId()).withLore("&eClick here to edit the loot").build());
            }


            return inventory;
        }

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
