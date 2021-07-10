package me.zxoir.lootchests.events;

import lombok.Getter;
import lombok.Setter;
import me.zxoir.lootchests.customclasses.LootChest;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/10/2021
 */
public class LootChestClaimEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;
    @Getter
    @Setter
    private ItemStack[] contents;
    @Getter
    private final Player player;
    @Getter
    private final Block block;
    @Getter
    private final LootChest lootChest;

    public LootChestClaimEvent(Player player, ItemStack[] contents, Block block, LootChest lootChest) {
        this.player = player;
        this.isCancelled = false;
        this.contents = contents;
        this.block = block;
        this.lootChest = lootChest;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
