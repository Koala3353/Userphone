package com.general_hello.commands.commands.Guild;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.general_hello.commands.Database.SQLiteDataSource;
import com.general_hello.commands.commands.RankingSystem.RoleReward;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import javax.annotation.CheckReturnValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GuildData {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuildData.class);

    private final long guildID;
    private final DataObject dataObject;


    public GuildData(long guildID, DataObject json)
    {
        this.guildID = guildID;
        this.dataObject = json;
    }

    public <T> T convertValueAt(String path, Class<T> type)
    {
        return dataObject.convertValueAt(path, type);
    }

    public GuildData put(String key, Object object)
    {
        dataObject.put(key, object);
        return this;
    }

    public String toJson() throws JsonProcessingException
    {
        return dataObject.toJson();
    }

    public String toPrettyString() throws JsonProcessingException
    {
        return dataObject.toPrettyString();
    }

    public GuildData putNull(String key)
    {
        dataObject.putNull(key);
        return this;
    }

    public GuildData setRoot(String root)
    {
        dataObject.setRoot(root);
        return this;
    }

    public String getRoot(String root)
    {
        return dataObject.getRoot();
    }

    public String getString(String query, Object... objects)
    {
        return dataObject.getString(query, objects);
    }

    public Integer getInt(String query)
    {
        return dataObject.getInt(query);
    }

    public Boolean getBoolean(String query)
    {
        return dataObject.getBoolean(query);
    }

    public Double getDouble(String query)
    {
        return dataObject.getDouble(query);
    }

    public Float getFloat(String query)
    {
        return dataObject.getFloat(query);
    }

    public Long getLong(String query)
    {
        return dataObject.getLong(query);
    }

    public Object getObject(String query)
    {
        return dataObject.getObject(query);
    }

    public <T> T get(String query, Class<T> type)
    {
        return dataObject.get(query, type);
    }

    public String[] getMetaData()
    {
        return dataObject.getMetadata();
    }

    public GuildData setMetaData(String[] metaData)
    {
        dataObject.setMetadata(metaData);
        return this;
    }

    // guild specific getters and setters

    public GuildData update() throws SQLException {
        Connection connection = SQLiteDataSource.getConnection();
        String query = "INSERT INTO guildSettings (guildID, data) values (?,?) ON DUPLICATE KEY UPDATE data = ?";
        try (PreparedStatement ps = connection.prepareStatement(query))
        {
            String jsonString = dataObject.toJson();
            ps.setLong(1, guildID);
            ps.setString(2, jsonString);
            ps.setString(3, jsonString);
            ps.execute();
        } catch (SQLException | JsonProcessingException exception)
        {
            LOGGER.error("Could not update guild data!", exception);
        } finally
        {
            GuildManager.closeQuietly(connection);
        }
        return this;
    }

    public String getPrefix()
    {
        return dataObject.getString("command_prefix");
    }

    public Set<RoleReward> getRoleRewards()
    {
        if (dataObject.getObject("role_rewards") == null)
            return Collections.emptySet();
        return Set.of(dataObject.convertValueAt("role_rewards", RoleReward[].class));
    }

    @CheckReturnValue
    public GuildData addRoleReward(int level, long roleId, boolean persist, boolean removeOnNextReward)
    {
        RoleReward roleReward = new RoleReward();
        roleReward.setLevel(level);
        roleReward.setRoleId(roleId);
        roleReward.setPersist(persist);
        roleReward.setRemoveOnNextReward(removeOnNextReward);
        Set<RoleReward> currentRewards = new HashSet<>(getRoleRewards());
        if (hasRoleReward(level))
            currentRewards.remove(getRoleReward(level));
        currentRewards.add(roleReward);
        dataObject.put("role_rewards", currentRewards);
        return this;
    }

    @CheckReturnValue
    public GuildData removeRoleReward(int level)
    {
        if (!hasRoleReward(level)) return this;
        Set<RoleReward> currentRewards = new HashSet<>(getRoleRewards());
        currentRewards.removeIf(reward -> reward.getLevel() == level);
        dataObject.put("role_rewards", currentRewards);
        return this;
    }

    public RoleReward getRoleReward(int level)
    {
        return getRoleRewards().stream().filter(reward -> reward.getLevel() == level).findFirst().orElse(null);
    }

    public boolean hasRoleReward(int level)
    {
        return getRoleRewards().stream().anyMatch(reward -> reward.getLevel() == level);
    }

    public RoleReward getLastRoleReward(int starting)
    {
        if (starting < 1) return null;
        Set<RoleReward> rewards = getRoleRewards();
        AtomicInteger integer = new AtomicInteger(starting);
        while (integer.get() > 0)
        {
            RoleReward reward = rewards.stream().filter(reward1 -> reward1.getLevel() == integer.get()).findFirst().orElse(null);
            if (reward != null)
                return reward;
            integer.decrementAndGet();
        }
        return null;
    }

    public Set<RoleReward> getAllRoleRewardsUpTo(int level)
    {
        if (level < 1) return Collections.emptySet();
        return getRoleRewards().stream().filter(reward -> reward.getLevel() <= level).collect(Collectors.toSet());
    }

    /**
     * Returns a {@link java.util.Set Set} of {@link RoleReward RoleRewards} that should be applied to a member with a given level.
     * This is not the same as {@link #getAllRoleRewardsUpTo(int)}!
     *
     * @param level The level
     * @return A Set of RoleRewards
     */
    public Set<RoleReward> getEffectiveRoleRewards(int level)
    {
        List<RoleReward> rewardList = new ArrayList<>(getAllRoleRewardsUpTo(level));
        if (rewardList.isEmpty())
            return Collections.emptySet();
        rewardList.sort(Comparator.comparingInt(RoleReward::getLevel));
        Set<RoleReward> result = new HashSet<>();
        RoleReward previous = null;
        for (RoleReward reward : rewardList)
        {
            if (previous != null && previous.doesRemoveOnNextReward())
            {
                result.remove(previous);
            }
            result.add(reward);
            previous = reward;
        }
        return result;
    }
}
