package com.general_hello.commands.commands.RankingSystem;

import com.general_hello.commands.Database.SQLiteDataSource;
import com.general_hello.commands.SlashCommands.SlashCommand;
import com.general_hello.commands.commands.Utils.Util;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class XPAlertCommand extends SlashCommand
{
    public XPAlertCommand()
    {
        setCommandData(new CommandData("setxpalerts", "Changes XP levelup behaviour")
                .addSubcommands(new SubcommandData("none", "disables xp alerts entirely"))
                .addSubcommands(new SubcommandData("dm", "notifies the user via dm when they level up"))
                .addSubcommands(new SubcommandData("current", "notifies the user in the current chat when they level up"))
                .addSubcommands(new SubcommandData("channel", "notifies the user in a specified channel when they level up")
                        .addOption(OptionType.CHANNEL, "targetchannel", "the channel where level-ups get logged", true)
                )
        );
        setRequiredUserPermissions(Permission.ADMINISTRATOR);
    }

    @Override
    public void executeCommand(@NotNull SlashCommandEvent event, @Nullable Member sender, @NotNull SlashCommandContext ctx) throws SQLException {
        Guild guild = event.getGuild();
        switch (event.getSubcommandName())
        {
            case "none":
                boolean a = setXPAlert(guild, "none");
                if (a)
                    ctx.reply("XP alerts have been successfully disabled!").setEphemeral(true).queue();
                else
                    ctx.replyError("Could not disable XP alerts!").setEphemeral(true).queue();
                return;
            case "dm":
                boolean b = setXPAlert(guild, "dm");
                if (b)
                    ctx.reply("XP alerts have been set to **DM**").setEphemeral(true).queue();
                else
                    ctx.replyError("Could not set XP alert mode!").setEphemeral(true).queue();
                return;
            case "current":
                boolean c = setXPAlert(guild, "current");
                if (c)
                    ctx.reply("XP alerts have been set to **current channel**").setEphemeral(true).queue();
                else
                    ctx.replyError("Could not set XP alert mode!").setEphemeral(true).queue();
                return;
            case "channel":
                GuildChannel channel = event.getOption("targetchannel").getAsGuildChannel();
                if (channel.getType() != ChannelType.TEXT)
                {
                    ctx.replyError("Can only use text-channels as XP alert target!").setEphemeral(true).queue();
                    return;
                }
                boolean d = setXPAlert(guild, channel.getId());
                if (d)
                    ctx.reply("XP alerts are now sent in **" + channel.getName() + "**").setEphemeral(true).queue();
                else
                    ctx.reply("Could not set XP alert mode!").setEphemeral(true).queue();
                return;
            default:
                ctx.replyError("Unknown target!").setEphemeral(true).queue();
        }
    }


    public static String getXPAlert(@Nonnull Guild guild) throws SQLException {
        Connection connection = SQLiteDataSource.getConnection();
        try (PreparedStatement ps = connection.prepareStatement("SELECT mode FROM xpAlerts WHERE guildID = ?"))
        {
            ps.setLong(1, guild.getIdLong());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("mode");
            return "current";
        } catch (SQLException ex)
        {
            ex.printStackTrace();
            return "none";
        } finally
        {
            Util.closeQuietly(connection);
        }
    }

    public static void sendXPAlert(@Nonnull Member member, int level, TextChannel current) throws SQLException {
        String mode = getXPAlert(member.getGuild());
        switch (mode)
        {
            case "none":
                return;
            case "dm":
                String message = "Hey " + member.getAsMention() + ", you just ranked up to level ** " + level + "**!";
                Util.sendPM(member.getUser(), message);
                return;
            case "current":
                if (current == null) return;
                current.sendMessage("Hey " + member.getAsMention() + ", you just ranked up to level ** " + level + "**!").queue();
                return;
            default:
                TextChannel channel = member.getGuild().getTextChannelById(mode);
                if (channel == null) return;
                channel.sendMessage("Hey " + member.getAsMention() + ", you just ranked up to level ** " + level + "**!").queue();
        }
    }

    public static boolean setXPAlert(@Nonnull Guild guild, String modeOrChannelID) throws SQLException {
        Connection connection = SQLiteDataSource.getConnection();
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO xpAlerts (guildID, mode) VALUES (?,?) ON DUPLICATE KEY UPDATE mode = ?"))
        {
            ps.setLong(1, guild.getIdLong());
            ps.setString(2, modeOrChannelID);
            ps.setString(3, modeOrChannelID);
            ps.execute();
            return true;
        } catch (SQLException ex)
        {
            ex.printStackTrace();
            return false;
        } finally
        {
            Util.closeQuietly(connection);
        }
    }

}
