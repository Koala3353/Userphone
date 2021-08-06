package com.general_hello.commands.commands.RankingSystem;

import com.general_hello.commands.Database.DatabaseManager;
import com.general_hello.commands.Database.SQLiteDataSource;
import com.general_hello.commands.commands.Guild.GuildData;
import com.general_hello.commands.commands.PrefixStoring;
import com.general_hello.commands.commands.Utils.Util;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class OnGainXP extends ListenerAdapter {
    public static final long TIMEOUT = 60000;

    private static final ConcurrentHashMap<Long, Long> timeout = new ConcurrentHashMap<>();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        final long guildID = event.getGuild().getIdLong();
        String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, DatabaseManager.INSTANCE::getPrefix);

        if (event.getAuthor().isBot() || event.isWebhookMessage() || event.getMessage().getType().isSystem()) return;
        if (event.getMessage().getContentRaw().startsWith(prefix))
            return;

        long userID = event.getAuthor().getIdLong();
            if (timeout.containsKey(userID))
            {
                long lastXPAdditionAgo = System.currentTimeMillis() - timeout.get(userID);
                if (lastXPAdditionAgo > TIMEOUT)
                {
                    Connection connection = null;
                    try {
                        connection = SQLiteDataSource.getConnection();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                    if (connection == null) return;
                    long currentTotalXP = RankingSystem.getTotalXP(connection, guildID, userID);
                    int level = RankingSystem.getLevel(currentTotalXP);
                    long currentXP = currentTotalXP - RankingSystem.getTotalXPNeeded(level);
                    long xpLeft = RankingSystem.getXPToLevelUp(level);
                    int xpAmount = 15 + new Random().nextInt(11);
                    RankingSystem.addXP(connection, guildID, userID, xpAmount, event.getAuthor().getName(), event.getAuthor().getDiscriminator());
                    Util.closeQuietly(connection);
                    if (xpAmount + currentXP >= xpLeft)
                    {
                        try {
                            XPAlertCommand.sendXPAlert(event.getMember(), level + 1, event.getChannel());
                            System.out.println("Plus Xp!");
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        GuildData data = null;
                        try {
                            data = GuildManager.getGuildData(event.getGuild());
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        if (data.hasRoleReward(level + 1))
                        {
                            RoleReward reward = data.getRoleReward(level + 1);
                            Role role = event.getGuild().getRoleById(reward.getRoleId());
                            if (role != null)
                            {
                                event.getGuild().addRoleToMember(userID, role).queue(s ->
                                {
                                }, e ->
                                {
                                });
                                RoleReward oldReward = data.getLastRoleReward(level);
                                if (oldReward != null)
                                {
                                    if (oldReward.doesRemoveOnNextReward())
                                    {
                                        Role oldRole = event.getGuild().getRoleById(oldReward.getRoleId());
                                        if (oldRole != null)
                                        {
                                            event.getGuild().removeRoleFromMember(userID, oldRole).queue(s ->
                                            {
                                            }, e ->
                                            {
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                    timeout.put(userID, System.currentTimeMillis());
                }
            } else
            {
                Connection connection = null;
                try {
                    connection = SQLiteDataSource.getConnection();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                if (connection == null) return;
                long currentTotalXP = RankingSystem.getTotalXP(connection, guildID, userID);
                int level = RankingSystem.getLevel(currentTotalXP);
                long currentXP = currentTotalXP - RankingSystem.getTotalXPNeeded(level);
                long xpLeft = RankingSystem.getXPToLevelUp(level);
                int xpAmount = 15 + new Random().nextInt(11);
                RankingSystem.addXP(connection, guildID, userID, xpAmount, event.getAuthor().getName(), event.getAuthor().getDiscriminator());
                Util.closeQuietly(connection);
                if (xpAmount + currentXP >= xpLeft)
                {
                    try {
                        XPAlertCommand.sendXPAlert(event.getMember(), level + 1, event.getChannel());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    GuildData data = null;
                    try {
                        data = GuildManager.getGuildData(event.getGuild());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    if (data.hasRoleReward(level + 1))
                    {
                        RoleReward reward = data.getRoleReward(level + 1);
                        Role role = event.getGuild().getRoleById(reward.getRoleId());
                        if (role != null)
                        {
                            event.getGuild().addRoleToMember(userID, role).queue(s ->
                            {
                            }, e ->
                            {
                            });
                            RoleReward oldReward = data.getLastRoleReward(level);
                            if (oldReward != null)
                            {
                                if (oldReward.doesRemoveOnNextReward())
                                {
                                    Role oldRole = event.getGuild().getRoleById(oldReward.getRoleId());
                                    if (oldRole != null)
                                    {
                                        event.getGuild().removeRoleFromMember(userID, oldRole).queue(s ->
                                        {
                                        }, e ->
                                        {
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
                timeout.put(userID, System.currentTimeMillis());
            }
    }
}
