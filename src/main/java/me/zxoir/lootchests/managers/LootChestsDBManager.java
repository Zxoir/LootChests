package me.zxoir.lootchests.managers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.zxoir.lootchests.customclasses.LootChest;
import me.zxoir.lootchests.customclasses.SerializableLootChest;
import me.zxoir.lootchests.utils.LootChestsDB;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MIT License Copyright (c) 2021 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
public class LootChestsDBManager {
    private static final Gson gson = new Gson();

    @NotNull
    protected static List<LootChest> getLootChests() {
        List<LootChest> lootChests = new CopyOnWriteArrayList<>();

        try {
            LootChestsDB.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM LootChests");
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    lootChests.add(dbToLootChest(resultSet));
                }

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: LCDM_GLCs.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: LCDM_GLCs.02");
            }
        }

        return lootChests;
    }

    @Nullable
    protected static LootChest getLootChestByID(int id) {

        AtomicReference<LootChest> lootChest = new AtomicReference<>(null);

        try {
            LootChestsDB.execute(conn -> {
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM LootChests WHERE id = ? LIMIT 1");
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();

                if (!resultSet.next())
                    return;

                lootChest.set(dbToLootChest(resultSet));

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: LCDM_GLC.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: LCDM_GLC.02");
            }
        }

        return lootChest.get();
    }

    @NotNull
    @Contract("_ -> new")
    protected static CompletableFuture<Void> saveLootChestToDB(SerializableLootChest lootChest) {
        return LootChestsDB.execute(conn -> {
            PreparedStatement statement = conn.prepareStatement("INSERT INTO LootChests(lootchestData) VALUES(?)");
            statement.setString(1, gson.toJson(lootChest));
            statement.execute();
        });
    }

    @NotNull
    @Contract("_ -> new")
    protected static CompletableFuture<Void> deleteLootChestFromDB(int id) {
        return LootChestsDB.execute(conn -> {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM LootChests WHERE id=?");
            statement.setInt(1, id);
            statement.execute();
        });
    }

    @NotNull
    @Contract("_ -> new")
    protected static CompletableFuture<Void> updateLootChest(SerializableLootChest lootChest) {
        return LootChestsDB.execute(conn -> {
            PreparedStatement statement = conn.prepareStatement(
                    "UPDATE LootChests SET lootchestData = ? WHERE id = ?");
            statement.setString(1, gson.toJson(lootChest));
            statement.setInt(2, lootChest.getId());
            statement.execute();
        });
    }

    protected static int getAutoIncrementValue() {
        try {
            AtomicReference<Integer> result = new AtomicReference<>(-1);

            LootChestsDB.execute(conn -> {
                ResultSet resultSet = conn.prepareStatement("SELECT SEQ from sqlite_sequence WHERE name='LootChests'").executeQuery();
                if (resultSet.next())
                    result.set(resultSet.getInt(1));
                else
                    result.set(0);
            }).get(5000, TimeUnit.MILLISECONDS);

            if (result.get() == -1) {
                throw new NullPointerException("ERROR: Could not get data from DataBase! Code: LCDM_GAIV.04");
            }

            return result.get();

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: LCDM_GAIV.01");
            } else if (e instanceof ExecutionException) {
                e.printStackTrace();
                e.getCause().printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: LCDM_GAIV.02");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution has timed out! Code: LCDM_GAIV.03");
            }
        }
    }

    @NotNull
    private static LootChest dbToLootChest(@NotNull ResultSet resultSet) throws SQLException {
        SerializableLootChest serializableLootChest = gson.fromJson(resultSet.getString("lootchestData"), new TypeToken<SerializableLootChest>() {
        }.getType());
        return serializableLootChest.getDeserializedLootChest();
    }
}
