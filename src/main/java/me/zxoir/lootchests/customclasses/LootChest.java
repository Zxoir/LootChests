package me.zxoir.lootchests.customclasses;

import lombok.Getter;
import lombok.Setter;
import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.managers.LootChestManager;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
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
    private @NotNull
    @Getter
    final LinkedList<Loot> loots;
    @Getter
    private final @NotNull LinkedHashMap<Location, BlockFace> locations;
    @Getter
    private final int id;
    @Getter
    @NotNull
    private Long interval;
    @Getter
    @Setter
    @NotNull
    private LootChests.LootChestType type;
    @Getter
    private int lootAmount;
    @Getter
    @Setter
    private boolean disabled;
    @Getter
    @Setter
    private boolean claimed;
    private int totalWeight;
    @Getter
    private final SpawnTask spawnTask;

    public LootChest(@NotNull Long interval, @NotNull LootChests.LootChestType type, int lootAmount) {
        this.interval = interval;
        this.type = type;
        this.lootAmount = lootAmount;
        this.id = LootChestManager.getNewID();
        loots = new LinkedList<>();
        locations = new LinkedHashMap<>();
        spawnTask = new SpawnTask(this);
        spawnTask.runTaskTimerAsynchronously(LootChests.getInstance(), 0, 5);
        disabled = false;
        claimed = false;
    }

    public LootChest(@NotNull Long interval, @NotNull LootChests.LootChestType type, @NotNull LinkedList<Loot> loots, @NotNull LinkedHashMap<Location, BlockFace> locations, int lootAmount, int totalWeight, int id) {
        this.interval = interval;
        this.type = type;
        this.loots = loots;
        this.locations = locations;
        this.lootAmount = lootAmount;
        this.totalWeight = totalWeight;
        this.id = id;
        spawnTask = new SpawnTask(this);
        spawnTask.runTaskTimerAsynchronously(LootChests.getInstance(), 0, 5);
        disabled = false;
        claimed = false;
    }

    public void addLoot(Loot loot) {
        loots.add(loot);
        totalWeight += loot.getChance();
        LootChestManager.updateLootChest(this);
    }

    public void removeLoot(Loot loot) {
        loots.remove(loot);
        totalWeight -= loot.getChance();
        LootChestManager.updateLootChest(this);
    }

    public Loot generateLoot() {
        int currentItemUpperBound = 0;

        int nextValue = random.nextInt(totalWeight);
        for (Loot itemAndWeight : loots) {
            currentItemUpperBound += itemAndWeight.getChance();
            if (nextValue < currentItemUpperBound)
                return itemAndWeight;
        }

        return loots.get(loots.size() - 1);
    }

    public void addLocation(Location location, BlockFace blockFace) {
        locations.put(location, blockFace);
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

    public void delete() {
        LootChestManager.deleteLootChest(this);
    }
}
