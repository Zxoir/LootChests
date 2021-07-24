package me.zxoir.lootchests.commands;

import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.managers.ConfigManager;
import me.zxoir.lootchests.managers.LootChestManager;
import me.zxoir.lootchests.utils.LootHolder;
import me.zxoir.lootchests.utils.LootsEditorHolder;
import me.zxoir.lootchests.utils.TimeManager;
import me.zxoir.lootchests.utils.Utils;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.management.remote.JMXServerErrorException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.zxoir.lootchests.utils.Utils.colorize;
import static me.zxoir.lootchests.utils.Utils.isInteger;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2021
 */
@SuppressWarnings("deprecation")
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

        if (args.length >= 3 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("normal")) {
            Long interval = new TimeManager(args[2]).toMilliSecond();
            if (interval == null || interval <= 0) {
                player.sendMessage(colorize("&cThe interval must be over 0!"));
                return true;
            }

            LootChest lootChest = new LootChest(interval, LootChests.LootChestType.NORMAL, 1);

            if (LootChestManager.registerLootChest(lootChest)) {
                TextComponent message = new TextComponent(colorize("&aSuccessfully created a new LootChest with ID " + lootChest.getId()));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lootchest edit " + lootChest.getId()));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(colorize("&aClick here to run the command"))));

                player.spigot().sendMessage(message);
            }
            else
                player.sendMessage(colorize("&cFailed to create new LootChest"));
        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("status") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }
            LootChest lootChest = LootChestManager.getLootChests().get(id);
            boolean spawnIsNull = lootChest.getSpawnTask().getLastSpawned() == null;

            String message = String.join("\n", LootChests.getInstance().getConfig().getStringList("lootchest_status"));
            message = message
                    .replace("%id%", "" + lootChest.getId())
                    .replace("%location%", (spawnIsNull ? "N/A" : locationToString(lootChest.getSpawnTask().getLastSpawned().getLocation())))
                    .replace("%isClaimed%", "" + lootChest.isClaimed())
                    .replace("%isDisabled%", "" + lootChest.isDisabled());
            if (message.isEmpty())
                return true;

            player.sendMessage(colorize(message));
        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("enable") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }
            LootChest lootChest = LootChestManager.getLootChests().get(id);

            lootChest.setDisabled(false);
            player.sendMessage(colorize("&aLootChest enabled."));
        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("disable") && isInteger(args[1])) {
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

        else if (args.length == 2 && args[0].equalsIgnoreCase("forceSpawn") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }
            LootChest lootChest = LootChestManager.getLootChests().get(id);

            lootChest.getSpawnTask().setSpawn(Instant.now());
            player.sendMessage(colorize("&aLootChest has been spawned."));
        }

        else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            if (LootChestManager.getLootChests().isEmpty()) {
                player.sendMessage(colorize("&cThere aren't any LootChests available."));
                return true;
            }

            player.sendMessage(colorize("&b&lList of LootChest ID's:"));
            LootChestManager.getLootChests().values().forEach(lootChest -> player.sendMessage(colorize("&7- &a" + lootChest.getId())));
        }

        else if (args.length == 3 && args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("random")) {
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

        else if (args.length == 1 && args[0].equalsIgnoreCase("edit")) {
            if (!LootChestManager.getEditLocations().containsKey(player)) {
                player.sendMessage(colorize("&cYou must edit a lootchest using &e/lootchest edit <id>"));
                return true;
            }
            LootChest lootChest = LootChestManager.getEditLocations().get(player);
            LootChestManager.getEditLocations().remove(player);
            player.sendMessage(colorize("&aYou are no longer editing a lootchest"));

            if (lootChest.getLocations().isEmpty()) return true;
            if (lootChest.getType().equals(LootChests.LootChestType.NORMAL)) {
                Optional<Location> firstKey = lootChest.getLocations().keySet().stream().findFirst();
                if (!firstKey.isPresent()) return true;
                if (!firstKey.get().getBlock().getType().equals(Material.CHEST)) {
                    Block block = firstKey.get().getBlock();
                    firstKey.get().getBlock().setType(Material.CHEST);
                    BlockData blockData = block.getBlockData();
                    if (blockData instanceof Directional) {
                        ((Directional) blockData).setFacing(lootChest.getLocations().get(firstKey.get()));
                        block.setBlockData(blockData);
                    }
                }

                Chest chest = (Chest) firstKey.get().getBlock().getState();
                chest.getPersistentDataContainer().set(new NamespacedKey(LootChests.getInstance(), "LootChest"), PersistentDataType.INTEGER, lootChest.getId());
                chest.update();
                return true;
            }
            for (Location location : lootChest.getLocations().keySet()) {
                Block block = location.getBlock();
                if (!block.getType().equals(Material.CHEST)) continue;
                block.setType(Material.AIR);
            }
        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("edit") && isInteger(args[1])) {
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
            for (Location location : lootChest.getLocations().keySet()) {
                Block block = location.getBlock();
                if (!block.getType().equals(Material.AIR)) continue;
                block.setType(Material.CHEST);
                BlockData blockData = block.getBlockData();
                if (blockData instanceof Directional) {
                    ((Directional) blockData).setFacing(lootChest.getLocations().get(location));
                    block.setBlockData(blockData);
                }
            }
        }

        else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && isInteger(args[1]) && args[2].equalsIgnoreCase("lootamount") && isInteger(args[3])) {
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

        else if (args.length == 4 && args[0].equalsIgnoreCase("edit") && isInteger(args[1]) && args[2].equalsIgnoreCase("interval")) {
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

        else if (args.length == 3 && args[0].equalsIgnoreCase("edit") && isInteger(args[1]) && args[2].equalsIgnoreCase("loot")) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }

            LootChest lootChest = LootChestManager.getLootChests().get(id);
            LootsEditorHolder lootsEditorHolder = new LootsEditorHolder(null, lootChest, false);
            player.openInventory(lootsEditorHolder.getInventory());
        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("edit") && args[1].equalsIgnoreCase("loots")) {
            if (LootChestManager.getLootChests().isEmpty()) {
                player.sendMessage(colorize("&cThere aren't any LootChests"));
                return true;
            }

            LootsEditorHolder lootsEditorHolder = new LootsEditorHolder(null, null, false);
            player.openInventory(lootsEditorHolder.getInventory());
        }

        else if (args.length == 3 && args[0].equalsIgnoreCase("addloot") && isInteger(args[1]) && isInteger(args[2])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }

            LootChest lootChest = LootChestManager.getLootChests().get(id);
            Inventory inventory = Bukkit.createInventory(new LootHolder(player, lootChest, Integer.parseInt(args[2])), 27, colorize("&aAdd your loot"));
            player.openInventory(inventory);
        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("delete") && isInteger(args[1])) {
            int id = Integer.parseInt(args[1]);
            if (!LootChestManager.getLootChests().containsKey(id)) {
                player.sendMessage(colorize("&cThat's an invalid ID."));
                return true;
            }

            LootChest lootChest = LootChestManager.getLootChests().get(id);
            lootChest.delete();
            player.sendMessage(colorize("&aLootChest successfully deleted."));
        }

        else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            LootChests.getInstance().reloadConfig();
            player.sendMessage(colorize("&aSuccessfully reloaded Config."));
        }

        else {
            player.sendMessage(colorize("&3[&bLootChest&3] &7List of commands:\n" +
                    "&e- &8/lootchests status <id>" +
                    "&e- &8/lootchests disable <id>" +
                    "&e- &8/lootchests enable <id>" +
                    "&e- &8/lootchests list" +
                    "&e- &8/lootchests forceSpawn <id>" +
                    "&e- &8/lootchests delete <id>" +
                    "&e- &8/lootchests create normal <id> <interval>" +
                    "&e- &8/lootchests create random <id> <interval>" +
                    "&e- &8/lootchests addloot <id> <chance>" +
                    "&e- &8/lootchests edit <id>" +
                    "&e- &8/lootchests edit <id> lootamount <amount>" +
                    "&e- &8/lootchests edit <id> interval <interval>" +
                    "&e- &8/lootchests edit <id> loot" +
                    "&e- &8/lootchests edit loots" +
                    "&e- &8/lootchests reload"));
        }

        return true;
    }

    private String locationToString(Location location) {
        return location.getX() + ", " + location.getY() + ", " + location.getZ();
    }
}