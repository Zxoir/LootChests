package me.zxoir.lootchests.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/1/2021
 */
@AllArgsConstructor
@Getter
@Setter
public class Loot {
    private ItemStack[] itemStacks;
    private int chance;
}
