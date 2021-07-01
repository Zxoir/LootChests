package me.zxoir.lootchests.customclasses;

import lombok.Getter;
import me.zxoir.lootchests.LootChests;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Random;

/**
 * MIT License Copyright (c) 2020 Zxoir
 *
 * @author Zxoir
 * @since 7/1/2021
 */
public class LootChest {
    private static final Random random = new Random();
    @Getter
    @NotNull
    Long interval;
    @Getter
    @NotNull
    LootChests.LootChestType type;
    @NotNull LinkedList<Loot> loots;
    @Getter
    @NotNull LinkedList<Location> locations;
    @Getter
    int lootAmount;
    int totalWeight;

    public LootChest(@NotNull Long interval, @NotNull LootChests.LootChestType type, int lootAmount) {
        this.interval = interval;
        this.type = type;
        this.lootAmount = lootAmount;
        loots = new LinkedList<>();
        locations = new LinkedList<>();
    }

    public void addLoot(Loot loot, int chance){
        loots.add(loot);
        totalWeight += chance;
    }

    public void removeLoot(Loot loot) {
        loots.remove(loot);
        totalWeight -= loot.getChance();
    }

    public Loot generateLoot() { // Todo: Test
        int currentItemUpperBound = 0;

        int nextValue = random.nextInt(totalWeight);
        for (Loot itemAndWeight : loots) {
            currentItemUpperBound += itemAndWeight.getChance();
            if (nextValue < currentItemUpperBound)
                return itemAndWeight;
        }

        return loots.get(loots.size() - 1);
    }
}
