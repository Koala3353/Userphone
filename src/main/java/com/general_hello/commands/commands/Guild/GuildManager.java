package com.general_hello.commands.commands.Guild;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.general_hello.commands.Database.SQLiteDataSource;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GuildManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildManager.class);

    private static final Map<Long, GuildData> GUILD_DATA = new ConcurrentHashMap<>();

    public static GuildData getGuildData(Guild guild) throws SQLException {
        if (GUILD_DATA.containsKey(guild.getIdLong()))
            return GUILD_DATA.get(guild.getIdLong());
        GuildData retrievedData = retrieveGuildData(guild.getIdLong());
        if (retrievedData != null)
        {
            GUILD_DATA.put(guild.getIdLong(), retrievedData);
            return retrievedData;
        }
        GuildData createdData = createGuildData(guild);
        GUILD_DATA.put(guild.getIdLong(), createdData);
        return createdData;
    }

    public static String getGuildDataJSON(long guildID) throws JsonProcessingException
    {
        if (!GUILD_DATA.containsKey(guildID)) return null;
        return GUILD_DATA.get(guildID).toPrettyString();
    }

    // guildSettings (guildID BIGINT PRIMARY KEY, data JSON CHECK (JSON_VALID(data)))
    private static GuildData retrieveGuildData(long guildID) throws SQLException {
        Connection connection = SQLiteDataSource.getConnection();
        String query = "SELECT data FROM guildSettings WHERE guildID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query))
        {
            ps.setLong(1, guildID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new GuildData(guildID, DataObject.parse(rs.getString("data")));
            return null;
        } catch (SQLException exception)
        {
            LOGGER.error("Could not retrieve guild data!", exception);
            return null;
        } finally
        {
            closeQuietly(connection);
        }
    }

    private static void updateGuildData(long guildID, com.general_hello.commands.commands.Guild.DataObject data) throws SQLException {
        Connection connection = SQLiteDataSource.getConnection();
        String query = "INSERT INTO guildSettings (guildID, data) values (?,?) ON DUPLICATE KEY UPDATE data = ?";
        try (PreparedStatement ps = connection.prepareStatement(query))
        {
            String jsonString = data.toJson();
            ps.setLong(1, guildID);
            ps.setString(2, jsonString);
            ps.setString(3, jsonString);
            ps.execute();
        } catch (SQLException | JsonProcessingException exception)
        {
            LOGGER.error("Could not update guild data!", exception);
        } finally
        {
            closeQuietly(connection);
        }
    }

    private static GuildData createGuildData(Guild guild) throws SQLException {
        com.general_hello.commands.commands.Guild.DataObject json = com.general_hello.commands.commands.Guild.DataObject.empty()
                .put("id", guild.getIdLong())
                .put("name", guild.getName())
                .put("owner", guild.getOwnerIdLong())
                .put("created", guild.getTimeCreated().toString())
                .put("joined", guild.getSelfMember().getTimeJoined().toString())
                .put("command_prefix", "+")
                .put("vip", false)
                .put("language", guild.getLocale().toString());

        updateGuildData(guild.getIdLong(), json);
        return new GuildData(guild.getIdLong(), json);
    }

    public static void closeQuietly(AutoCloseable... closeables)
    {
        for (AutoCloseable c : closeables)
        {
            if (c != null)
            {
                try
                {
                    c.close();
                } catch (Exception ignored)
                {
                }
            }
        }
    }
}
