package me.zxoir.lootchests.events;

import lombok.Getter;
import me.zxoir.lootchests.customclasses.Loot;
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
 * @since 7/7/2021
 */
public class RemoveLootEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;
    @Getter
    private final LootChest lootChest;
    @Getter
    private final Loot loot;
    @Getter
    private final Player player;

    public RemoveLootEvent(Player player, @NotNull Loot loot, @NotNull LootChest lootChest) {
        this.player = player;
        this.isCancelled = false;
        this.loot = loot;
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
