package me.zxoir.lootchests.managers;

/**
 * MIT License Copyright (c) 2020 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
public class LootChestManager {
    private static int ID;

    public static int getID() {
        return ID;
    }

    public static int getNewID() {
        return ++ID;
    }
}
