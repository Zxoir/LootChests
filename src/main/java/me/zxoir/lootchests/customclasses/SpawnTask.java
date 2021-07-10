package me.zxoir.lootchests.customclasses;

import lombok.Getter;
import lombok.Setter;
import me.zxoir.lootchests.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/7/2021
 */
public class SpawnTask extends BukkitRunnable {
    @Getter
    private final LootChest lootChest;
    private final Random random;
    @Getter
    @Setter
    private Instant spawn;
    @Getter
    private Block lastSpawned;
    @Getter
    private static final HashMap<Block, LootChest> chests = new HashMap<>();

    public SpawnTask(LootChest lootChest) {
        this.lootChest = lootChest;
        spawn = Instant.now().plusMillis(lootChest.getInterval());
        random = new Random();
    }

    @Override
    public void run() {
        if (Duration.between(Instant.now(), spawn).getSeconds() > 0) return;

        if (lootChest.getLocations().isEmpty()) return;

        if (lootChest.getLoots().isEmpty()) return;

        if (lootChest.isDisabled()) return;

        Location location = lootChest.getLocations().get(random.nextInt(lootChest.getLocations().size()));
        Loot loot = lootChest.generateLoot();
        Utils.runTaskSync(() -> {
            if (lastSpawned != null) {
                if (lastSpawned.getType().equals(Material.CHEST)) {
                    ((Chest) lastSpawned.getState()).getBlockInventory().clear();
                }
                lastSpawned.setType(Material.AIR);
            }

            if (location.getBlock().getType().equals(Material.CHEST)) {
                Chest chest = (Chest) location.getBlock().getState();
                chest.getBlockInventory().clear();
            }

            location.getBlock().setType(Material.CHEST);
            Chest chest = (Chest) location.getBlock().getState();
            Arrays.stream(loot.getItemStacks()).filter(item -> item != null && !item.getType().equals(Material.AIR)).forEach(item -> chest.getBlockInventory().addItem(item));
            chests.remove(lastSpawned);
            chests.put(location.getBlock(), lootChest);
            lastSpawned = location.getBlock();
            lootChest.setClaimed(false);
        });
        spawn = Instant.now().plusMillis(lootChest.getInterval());
    }
}
