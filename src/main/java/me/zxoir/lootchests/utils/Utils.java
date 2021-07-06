package me.zxoir.lootchests.utils;

import me.zxoir.lootchests.LootChests;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
public class Utils {
    @NotNull
    @Contract("_ -> new")
    public static String colorize(String arg) {
        return ChatColor.translateAlternateColorCodes('&', arg);
    }

    public interface Task {
        void execute();
    }

    public static boolean isStaff(Player player) {
        return player.hasPermission("lootchests.admin");
    }

    public static boolean isStaff(CommandSender sender) {
        return sender.hasPermission("lootchests.admin");
    }

    public static boolean isInteger(String key) {
        try {
            Integer.parseInt(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void runTaskSync(Task task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTask(LootChests.getInstance());
    }
}
