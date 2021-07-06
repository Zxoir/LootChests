package me.zxoir.lootchests.customclasses;

import lombok.Getter;
import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.managers.LootChestManager;
import me.zxoir.lootchests.managers.LootChestsDBManager;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Random;

/**
 * MIT License Copyright (c) 2021 Zxoir
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
    @Getter
    private final int id;

    public LootChest(@NotNull Long interval, @NotNull LootChests.LootChestType type, int lootAmount) {
        this.interval = interval;
        this.type = type;
        this.lootAmount = lootAmount;
        this.id = LootChestManager.getNewID();
        loots = new LinkedList<>();
        locations = new LinkedList<>();
    }

    public LootChest(@NotNull Long interval, @NotNull LootChests.LootChestType type, @NotNull LinkedList<Loot> loots, @NotNull LinkedList<Location> locations, int lootAmount, int totalWeight, int id) {
        this.interval = interval;
        this.type = type;
        this.loots = loots;
        this.locations = locations;
        this.lootAmount = lootAmount;
        this.totalWeight = totalWeight;
        this.id = id;
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

    public void addLocation(Location location) {
        locations.add(location);
        LootChestManager.updateLootChest(this);
    }

    public void removeLocation(Location location) {
        locations.remove(location);
        LootChestManager.updateLootChest(this);
    }

    public void setLootAmount(int lootAmount) {
        this.lootAmount = lootAmount;
        LootChestManager.updateLootChest(this);
    }

    public void setInterval(Long interval) {
        this.interval = interval;
        LootChestManager.updateLootChest(this);
    }

    public SerializableLootChest getSerializedLootChest() {
        return new SerializableLootChest(interval, type, loots, locations, lootAmount, totalWeight, id);
    }
}
