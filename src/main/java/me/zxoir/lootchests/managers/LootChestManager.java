package me.zxoir.lootchests.managers;

import lombok.Getter;
import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.customclasses.LootChest;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;

import static me.zxoir.lootchests.utils.Utils.colorize;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.CHEST;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
@SuppressWarnings({"deprecation", "unused"})
public class LootChestManager {
    @Getter
    private static final LinkedHashMap<Integer, LootChest> lootChests = new LinkedHashMap<>();
    @Getter
    private static final HashMap<Player, LootChest> editLocations = new HashMap<>();
    private static boolean setup = false;
    private static int ID = -1;

    public static void setup() {
        if (setup) return;
        ID = LootChestsDBManager.getAutoIncrementValue();
        LootChestsDBManager.getLootChests().forEach(lootChest -> lootChests.put(lootChest.getId(), lootChest));

        for (LootChest lootChest : lootChests.values()) {
            if (lootChest.getType().equals(LootChests.LootChestType.NORMAL)) {
                Optional<Location> firstKey = lootChest.getLocations().keySet().stream().findFirst();
                if (!firstKey.isPresent()) continue;
                Block block = firstKey.get().getBlock();
                BlockData blockData = block.getBlockData();
                if (block.getType().equals(AIR)) block.setType(CHEST);
                if (blockData instanceof Directional) {
                    ((Directional) blockData).setFacing(lootChest.getLocations().get(firstKey.get()));
                    block.setBlockData(blockData);
                }
                lootChest.getSpawnTask().setSpawn(Instant.now());
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                if (editLocations.isEmpty()) {
                    return;
                }

                for (@NotNull Player player : editLocations.keySet()) {
                    if (!player.isOnline()) {
                        editLocations.remove(player);
                        return;
                    }

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colorize("&8Set all LootChest locations &c(Place a Chest)")));
                }

            }

        }.runTaskTimerAsynchronously(LootChests.getInstance(), 0, 10);

        setup = true;
    }

    public static boolean registerLootChest(@NotNull LootChest lootChest) {
        if (lootChests.containsKey(lootChest.getId())) return false;
        LootChestsDBManager.saveLootChestToDB(lootChest.getSerializedLootChest());
        lootChests.put(lootChest.getId(), lootChest);
        return true;
    }

    public static void updateLootChest(@NotNull LootChest lootChest) {
        LootChestsDBManager.updateLootChest(lootChest.getSerializedLootChest());
    }

    public static void deleteLootChest(@NotNull LootChest lootChest) {
        lootChest.getSpawnTask().cancel();
        Block block = lootChest.getSpawnTask().getLastSpawned();
        if (block != null && block.getType().equals(CHEST)) {
            ((Chest) block.getState()).getBlockInventory().clear();
            block.setType(AIR);
        }
        LootChestsDBManager.deleteLootChestFromDB(lootChest.getId());
        lootChests.remove(lootChest.getId());
    }

    public static int getID() {
        return ID;
    }

    public static int getNewID() {
        return ++ID;
    }
}
