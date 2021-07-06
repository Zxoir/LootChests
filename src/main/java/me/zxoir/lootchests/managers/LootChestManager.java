package me.zxoir.lootchests.managers;

import lombok.Getter;
import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.customclasses.LootChest;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

import static me.zxoir.lootchests.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
public class LootChestManager {
    @Getter
    private static final HashMap<Integer, LootChest> lootChests = new HashMap<>();
    @Getter
    private static final HashMap<Player, LootChest> editLocations = new HashMap<>();
    private static boolean setup = false;
    private static int ID = -1;

    public static void setup() {
        if (setup) return;
        ID = LootChestsDBManager.getAutoIncrementValue();
        LootChestsDBManager.getLootChests().forEach(lootChest -> lootChests.put(lootChest.getId(), lootChest));

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

                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colorize("&8Set all LootChest locations &c(Place Redstone Blocks)")));
                }

            }

        }.runTaskTimerAsynchronously(LootChests.getInstance(), 0, 10);

        setup = true;
    }

    public static boolean registerLootChest(@NotNull LootChest lootChest) {
        if (lootChests.containsKey(lootChest.getId())) return false;
        LootChestsDBManager.saveLootChestToDB(lootChest.getSerializedLootChest());
        return true;
    }

    public static void updateLootChest(@NotNull LootChest lootChest) {
        LootChestsDBManager.updateLootChest(lootChest.getSerializedLootChest());
    }

    public static int getID() {
        return ID;
    }

    public static int getNewID() {
        return ++ID;
    }
}
