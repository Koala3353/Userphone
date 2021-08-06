package com.general_hello.commands.commands.RankingSystem;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RoleReward {
    @JsonProperty("level")
    private int level;
    @JsonProperty("role_id")
    private long roleId;
    @JsonProperty("persists")
    private boolean persist;
    @JsonProperty("remove_on_next_reward")
    private boolean removeOnNextReward;

    @Override
    public int hashCode()
    {
        return Objects.hash(level);
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(long roleId)
    {
        this.roleId = roleId;
    }

    public boolean isPersist()
    {
        return persist;
    }

    public void setPersist(boolean persist)
    {
        this.persist = persist;
    }

    public boolean doesRemoveOnNextReward()
    {
        return removeOnNextReward;
    }

    public void setRemoveOnNextReward(boolean removeOnNextReward)
    {
        this.removeOnNextReward = removeOnNextReward;
    }
}
