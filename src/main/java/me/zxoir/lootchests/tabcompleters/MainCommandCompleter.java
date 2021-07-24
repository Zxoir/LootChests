package me.zxoir.lootchests.tabcompleters;

import me.zxoir.lootchests.managers.LootChestManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2021
 */
public class MainCommandCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return new ArrayList<>();
        Player player = (Player) sender;

        if (args.length == 1) {
            return smartComplete(args, new ArrayList<String>() {{
                add("status");
                add("disable");
                add("enable");
                add("list");
                add("forcespawn");
                add("delete");
                add("create");
                add("addloot");
                add("edit");
                add("reload");
            }});
        }

        if (args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("forceSpawn") ||
                args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("edit")) {
            return smartComplete(args, new ArrayList<String>() {{
                LootChestManager.getLootChests().forEach((integer, lootChest) -> add(lootChest.getId() + ""));
            }});
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length == 2) {
                return smartComplete(args, new ArrayList<String>() {{
                    add("random");
                    add("normal");
                }});
            }

            if (args.length == 3) {
                return smartComplete(args, new ArrayList<String>() {{
                    add("<interval>");
                }});
            }
        }

        if (args[0].equalsIgnoreCase("addloot")) {
            if (args.length == 2) {
                return smartComplete(args, new ArrayList<String>() {{
                    LootChestManager.getLootChests().forEach((integer, lootChest) -> add(lootChest.getId() + ""));
                }});
            }

            if (args.length == 3) {
                return smartComplete(args, new ArrayList<String>() {{
                    add("<chance>");
                }});
            }
        }

        if (args[0].equalsIgnoreCase("edit")) {
            if (args.length == 2) {
                return smartComplete(args, new ArrayList<String>() {{
                    LootChestManager.getLootChests().forEach((integer, lootChest) -> add(lootChest.getId() + ""));
                    add("loots");
                }});
            }

            if (args.length == 3) {
                return smartComplete(args, new ArrayList<String>() {{
                    add("lootamount");
                    add("interval");
                    add("loot");
                }});
            }

            if (args.length == 4 && args[2].equalsIgnoreCase("lootamount")) {
                return smartComplete(args, new ArrayList<String>() {{
                    add("<amount>");
                }});
            }

            if (args.length == 4 && args[2].equalsIgnoreCase("interval")) {
                return smartComplete(args, new ArrayList<String>() {{
                    add("<interval>");
                }});
            }
        }

        return new ArrayList<>();
    }

    private static List<String> smartComplete(String[] args, List<String> list) {
        String arg = args[args.length - 1];
        ArrayList<String> temp = new ArrayList<>();

        for (String item : list) {
            if (item.toUpperCase().startsWith(arg.toUpperCase())) {
                temp.add(item);
            }
        }

        return temp;
    }
}