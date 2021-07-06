package me.zxoir.lootchests.events;

import lombok.Getter;
import me.zxoir.lootchests.customclasses.LootChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/6/2021
 */
public class AddLootChestEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;
    @Getter
    private final Player player;
    @Getter
    private final LootChest lootChest;

    public AddLootChestEvent(@NotNull Player player, @NotNull LootChest lootChest) {
        this.isCancelled = false;
        this.player = player;
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
