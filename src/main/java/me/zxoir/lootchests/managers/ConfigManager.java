package me.zxoir.lootchests.managers;

import lombok.Getter;
import me.zxoir.lootchests.LootChests;

import static me.zxoir.lootchests.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2021
 */
public class ConfigManager {
    @Getter
    private static String noPermission;
    private static boolean setup = false;
    private final static LootChests mainInstance = LootChests.getInstance();

    public static void setup() {
        if (setup) return;

        noPermission = colorize(mainInstance.getConfig().getString("no_permission"));

        setup = true;
    }
}
