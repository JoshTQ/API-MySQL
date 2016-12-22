package com.tara3208.mysql;

import com.zaxxer.hikari.HikariDataSource;
import eu.manuelgu.discordmc.DiscordMC;
import eu.manuelgu.discordmc.MessageAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Tara3208 on 12/10/16.
 * This has been created privately.
 * Copyright applies. Breach of this is not warranted
 */
public class Main extends JavaPlugin {

    private HikariDataSource hikariDataSource;

    @Override
    public void onEnable() {
        setUp();
        addBan("Testing", Bukkit.getOfflinePlayer("Potters").getUniqueId(), BanLength.PERMANENT);
    }

    @Override
    public void onDisable() {

    }

    public void setUp() {
        hikariDataSource = new HikariDataSource();

        hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.addDataSourceProperty("serverName", "www.example.com");
        hikariDataSource.addDataSourceProperty("port", 3306);
        hikariDataSource.addDataSourceProperty("databaseName", "example");
        hikariDataSource.addDataSourceProperty("user", "example");
        hikariDataSource.addDataSourceProperty("password", "example");

    }

    public void addBan(final String reason,final UUID uuid, final BanLength banLength) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            public void run() {

                Connection connection = null;

                String statement = "INSERT INTO Bans VALUES (?, ?, ?)";

                PreparedStatement preparedStatement = null;

                try {
                    hikariDataSource.getConnection();

                    preparedStatement = connection.prepareStatement(statement);

                    preparedStatement.setString(1, reason);
                    preparedStatement.setString(2, uuid.toString());
                    preparedStatement.setString(3, banLength.toString());

                    preparedStatement.execute();

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } if (preparedStatement != null) {
                        try {
                            preparedStatement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        // Already running ASync thanks to the API (Open Source Spigot API)
        MessageAPI.sendToDiscord(DiscordMC.getClient().getGuilds().get(0).getChannelsByName("Bans"), "**" + uuid + "** has been banned for **" + reason + "** (" + banLength + ")");
    }



}
