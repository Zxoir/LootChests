package me.zxoir.lootchests.customclasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.lootchests.LootChests;
import me.zxoir.lootchests.utils.ItemDeserializer;
import me.zxoir.lootchests.utils.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
@Setter
@Getter
public class SerializableLootChest {
    Long interval;
    LootChests.LootChestType type;
    LinkedList<SerializableLoot> loots;
    LinkedHashMap<String, BlockFace> locations;
    int lootAmount;
    int totalWeight;
    int id;

    public SerializableLootChest(Long interval, LootChests.LootChestType type, LinkedList<Loot> loots, LinkedHashMap<Location, BlockFace> locations, int lootAmount, int totalWeight, int id) {
        this.interval = interval;
        this.type = type;
        this.lootAmount = lootAmount;
        this.totalWeight = totalWeight;
        this.id = id;

        LinkedList<SerializableLoot> serializableLoots = new LinkedList<>();
        for (Loot loot : loots) {
            serializableLoots.add(new SerializableLoot(ItemDeserializer.itemStackArrayToBase64(loot.getItemStacks()), loot.getChance()));
        }
        this.loots = serializableLoots;
        final Gson adapter = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();

        LinkedHashMap<String, BlockFace> serializableLocations = new LinkedHashMap<>();
        for (Location location : locations.keySet()) {
            serializableLocations.put(adapter.toJson(location, Location.class), locations.get(location));
        }
        this.locations = serializableLocations;
    }

    public LootChest getDeserializedLootChest() {
        final Gson adapter = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
        LinkedList<Loot> loots = new LinkedList<>();
        for (SerializableLoot loot : this.loots) {
            try {
                loots.add(new Loot(ItemDeserializer.itemStackArrayFromBase64(loot.getItemStacks()), loot.getChance()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LinkedHashMap<Location, BlockFace> locations = new LinkedHashMap<>();
        for (String location : this.locations.keySet()) {
            locations.put(adapter.fromJson(location, Location.class), this.locations.get(location));
        }

        return new LootChest(this.interval, this.type, loots, locations, this.lootAmount, this.totalWeight, this.id);
    }
}
