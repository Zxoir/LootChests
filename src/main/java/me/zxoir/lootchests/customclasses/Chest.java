package me.zxoir.lootchests.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/13/2021
 */
@AllArgsConstructor
@Getter
@Setter
public class Chest {
    private Location location;
    private BlockFace blockFace;
}
