package me.zxoir.lootchests.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.zxoir.lootchests.LootChests;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LootChestsDB {
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;
    private static boolean setup = false;

    public LootChestsDB(String sqlCreateStatement) {
        if (setup) return;

        LootChests.getLcLogger().info("Starting Database setup - SQLITE");
        long start = System.currentTimeMillis();
        final File dbFile = new File(LootChests.getInstance().getDataFolder(), "database.db");
        try {
            if (!dbFile.exists()) {
                if (dbFile.createNewFile())
                    LootChests.getLcLogger().info("Created database file." + SQLiteDataSource.class);
                else
                    LootChests.getLcLogger().warn("Could not create database file." + SQLiteDataSource.class);

            }

        } catch (IOException e) {
            LootChests.getLcLogger().warn("Failed to create database file.");
            e.printStackTrace();
        }

        config.setJdbcUrl("jdbc:sqlite:" + dbFile);
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource = new HikariDataSource(config);

        try {

            execute((connection) -> {
                PreparedStatement statement = connection.prepareStatement(sqlCreateStatement);
                statement.executeUpdate();
            }).get();

        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Thread was interrupted during execution! Code: SDB_SDB.01");
            } else {
                e.printStackTrace();
                throw new IllegalThreadStateException("ERROR: Execution was aborted during execution! Code: SDB_SDB.02");
            }
        }

        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;
        LootChests.getLcLogger().info("Completed DB setup in " + finish + " s");
        setup = true;
    }

    @NotNull
    @Contract("_ -> new")
    public static CompletableFuture<Void> execute(ConnectionCallback callback) {
        return CompletableFuture.runAsync(() -> {

            try (Connection conn = dataSource.getConnection()) {
                callback.doInConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new IllegalStateException("Error during execution.", e);
            }

        });
    }

    public interface ConnectionCallback {
        void doInConnection(Connection conn) throws SQLException;
    }
}
