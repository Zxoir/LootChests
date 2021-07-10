package me.zxoir.lootchests.commands;

import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.managers.ConfigManager;
import me.zxoir.lootchests.managers.LootChestManager;
import me.zxoir.lootchests.managers.LootManager;
import me.zxoir.lootchests.utils.LootHolder;
import me.zxoir.lootchests.utils.LootsEditorHolder;
import me.zxoir.lootchests.utils.TimeManager;
import me.zxoir.lootchests.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilderApplicable;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static me.zxoir.lootchests.utils.Utils.colorize;
import static me.zxoir.lootchests.utils.Utils.isInteger;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2021
 */
public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Only players can access this command.");
            return true;
        }

        Player player = (Player) sender;
        if (!Utils.isStaff(player)) {
            player.sendMessage(ConfigManager.getNoPermission());
            return true;
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("normal")) { // Todo: Change the system to not be like random chests
            Long interval = new TimeManager(args[2]).toMilliSecond();
            if (interval == null || interval <= 0) {
                player.sendMessage(colorize("&cThe interval must be over 0!"));
                return true;
            }

            LootChest lootChest = new LootChest(interval, LootChests.LootChestType.NORMAL, 1);

            if (LootChestManager.registerLootChest(lootChest)) {
                TextComponent message = new TextComponent(colorize("&aSuccessfully created a new LootChest with ID " + lootChest.getId() +
                        "\n&7To set the lootchests location, use &e/lootchest edit " + lootChest.getId()));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lootchest edit " + lootChest.getId()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(colorize("&aClick here to run the command"))));

                player.spigot().sendMessage(message);
            }
            else
                player.sendMessage(colorize("&cFailed to create new LootChest"));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("status") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }
            LootChest lootChest = LootChestManager.getLootChests().get(id);
            boolean spawnIsNull = lootChest.getSpawnTask().getLastSpawned() == null;

            player.sendMessage(colorize("&7&lCurrent status for LootChest " + lootChest.getId() + "\n" +
                    "&aLast spawned location: &8" + (spawnIsNull ? "N/A" : locationToString(lootChest.getSpawnTask().getLastSpawned().getLocation())) + "\n" +
                    "&aClaimed: &8" + lootChest.isClaimed() + "\n" +
                    "&aDisabled: &8" + lootChest.isDisabled()));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("enable") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }
            LootChest lootChest = LootChestManager.getLootChests().get(id);

            lootChest.setDisabled(false);
            player.sendMessage(colorize("&aLootChest enabled."));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("disable") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }
            LootChest lootChest = LootChestManager.getLootChests().get(id);

            if (!lootChest.isClaimed() && lootChest.getSpawnTask().getLastSpawned() != null && lootChest.getSpawnTask().getLastSpawned().getType().equals(Material.CHEST)) {
                Chest chest = (Chest) lootChest.getSpawnTask().getLastSpawned().getState();
                chest.getBlockInventory().clear();
                lootChest.getSpawnTask().getLastSpawned().setType(Material.AIR);
            }
            lootChest.setDisabled(true);
            player.sendMessage(colorize("&cLootChest disabled."));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("forceSpawn") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }
            LootChest lootChest = LootChestManager.getLootChests().get(id);

            lootChest.getSpawnTask().setSpawn(Instant.now());
            player.sendMessage(colorize("&aLootChest has been spawned."));
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            if (LootChestManager.getLootChests().isEmpty()) {
                player.sendMessage(colorize("&cThere aren't any LootChests available."));
                return true;
            }

            player.sendMessage(colorize("&b&lList of LootChest ID's:"));
            LootChestManager.getLootChests().values().forEach(lootChest -> player.sendMessage(colorize("&7- &a" + lootChest.getId())));
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("random")) {
            Long interval = new TimeManager(args[2]).toMilliSecond();
            if (interval == null || interval <= 0) {
                player.sendMessage(colorize("&cThe interval must be over 0!"));
                return true;
            }

            LootChest lootChest = new LootChest(interval, LootChests.LootChestType.RANDOM, 1);

            if (LootChestManager.registerLootChest(lootChest)) {
                TextComponent message = new TextComponent(colorize("&aSuccessfully created a new LootChest with ID " + lootChest.getId() +
                        "\n&7To set the lootchests location, use &e/lootchest edit " + lootChest.getId()));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lootchest edit " + lootChest.getId()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(colorize("&aClick here to run the command"))));

                player.spigot().sendMessage(message);
            }
            else
                player.sendMessage(colorize("&cFailed to create new LootChest"));
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("edit")) {
            if (!LootChestManager.getEditLocations().containsKey(player)) {
                player.sendMessage(colorize("&cYou must edit a lootchest using &e/lootchest edit <id>"));
                return true;
            }
            LootChest lootChest = LootChestManager.getEditLocations().get(player);
            LootChestManager.getEditLocations().remove(player);
            player.sendMessage(colorize("&aYou are no longer editing a lootchest"));

            if (lootChest.getLocations().isEmpty()) return true;
            for (Location location : lootChest.getLocations()) {
                Block block = location.getBlock();
                if (!block.getType().equals(Material.REDSTONE_BLOCK)) break;
                block.setType(Material.AIR);
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("edit") && isInteger(args[1])) {
            if (LootChestManager.getEditLocations().containsKey(player)) {
                player.sendMessage(colorize("&cYou are already editing a LootChest, to exit use &e/lootchests edit"));
            }

            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }

            LootChest lootChest = LootChestManager.getLootChests().get(id);
            LootChestManager.getEditLocations().put(player, lootChest);
            player.sendMessage(colorize("&7To exit use &e/lootchests edit"));

            if (lootChest.getLocations().isEmpty()) return true;
            for (Location location : lootChest.getLocations()) {
                Block block = location.getBlock();
                if (!block.getType().equals(Material.AIR)) break;
                block.setType(Material.REDSTONE_BLOCK);
            }
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("edit") && isInteger(args[1]) && args[2].equalsIgnoreCase("lootamount") && isInteger(args[3])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }

            LootChest lootChest = LootChestManager.getLootChests().get(id);
            int amount = Integer.parseInt(args[3]);
            if (amount <= 0) {
                player.sendMessage(colorize("&cThe loot amount must be 1 or above."));
                return true;
            }

            lootChest.setLootAmount(amount);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("edit") && isInteger(args[1]) && args[2].equalsIgnoreCase("interval")) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }

            LootChest lootChest = LootChestManager.getLootChests().get(id);
            Long interval = new TimeManager(args[3]).toMilliSecond();
            if (interval == null || interval <= 0) {
                player.sendMessage(colorize("&cThe interval must be over 0!"));
                return true;
            }


            lootChest.setInterval(interval);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("edit") && isInteger(args[1]) && args[2].equalsIgnoreCase("loot")) {
            LootsEditorHolder lootsEditorHolder = new LootsEditorHolder(null, null);
            player.openInventory(lootsEditorHolder.getInventory());
            player.sendMessage(lootsEditorHolder.getInventory().getHolder().getClass().getName());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("addloot") && isInteger(args[1]) && isInteger(args[2])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }

            LootChest lootChest = LootChestManager.getLootChests().get(id);
            Inventory inventory = Bukkit.createInventory(new LootHolder(player, lootChest, Integer.parseInt(args[2])), 27, colorize("&aAdd your loot"));
            player.openInventory(inventory);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("delete") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }

            LootChest lootChest = LootChestManager.getLootChests().get(id);
            lootChest.delete();
            player.sendMessage(colorize("&aLootChest successfully deleted."));
        }

        return true;
    }

    private String locationToString(Location location) {
        return location.getX() + ", " + location.getY() + ", " + location.getZ();
    }
}