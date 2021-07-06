package me.zxoir.lootchests.events;

import lombok.Getter;
import lombok.Setter;
import me.zxoir.lootchests.customclasses.LootChest;
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
 * @since 7/7/2021
 */
public class AddLootEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;
    @Getter
    @Setter
    private ItemStack[] contents;
    @Getter
    @Setter
    private int chance;
    @Getter
    private final Player player;

    public AddLootEvent(Player player, ItemStack[] contents, int chance) {
        this.player = player;
        this.isCancelled = false;
        this.contents = contents;
        this.chance = chance;
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
