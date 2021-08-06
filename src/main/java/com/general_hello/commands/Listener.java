package com.general_hello.commands;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.general_hello.commands.Database.DatabaseManager;
import com.general_hello.commands.Database.SQLiteDataSource;
import com.general_hello.commands.commands.Call.QueueDatabase;
import com.general_hello.commands.commands.GetData;
import com.general_hello.commands.commands.Info.InfoUserCommand;
import com.general_hello.commands.commands.PrefixStoring;
import com.general_hello.commands.commands.Register.Data;
import com.general_hello.commands.commands.Settings.SettingsData;
import com.general_hello.commands.commands.User.UserPhoneUser;
import com.general_hello.commands.commands.Utils.UtilString;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.BotCommons;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static com.general_hello.commands.commands.Utils.UtilBot.getStatusEmoji;

public class Listener extends ListenerAdapter {
    public static CommandManager manager;
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    public static HashMap<String, Integer> count = new HashMap<>();
    private static OffsetDateTime timeDisconnected = OffsetDateTime.now();
    public static JDA jda;

    public Listener(EventWaiter waiter) {
        manager = new CommandManager(waiter);
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        GetData getData = new GetData();
        getData.checkIfContainsData(event.getAuthor(), event);

        EmbedBuilder em;

        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        final long guildID = event.getGuild().getIdLong();
        String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, DatabaseManager.INSTANCE::getPrefix);
        String raw = event.getMessage().getContentRaw();

        if (QueueDatabase.retrieveCallId.containsKey(event.getChannel())) {
            Integer callID = QueueDatabase.retrieveCallId.get(event.getChannel());
            ArrayList<TextChannel> callChannels = QueueDatabase.activeCall.get(callID);
            TextChannel channel1 = event.getChannel();
            TextChannel channel2 = callChannels.get(0);

            if (channel1 == callChannels.get(0)) {
                channel2 = callChannels.get(1);
            }

            String url;

            if (Data.serverToWebhookUrl.containsKey(channel2)) {
                url = Data.serverToWebhookUrl.get(channel2);
            } else {
                url = channel2.createWebhook("Unknown user").complete().getUrl();
                Data.serverToWebhookUrl.put(channel2, url);
            }

            WebhookClientBuilder builder = new WebhookClientBuilder(url);
            builder.setThreadFactory((job) -> {
                Thread thread = new Thread(job);
                thread.setName("Hello");
                thread.setDaemon(true);
                return thread;
            });

            builder.setWait(true);
            WebhookClient client = builder.build();
            WebhookMessageBuilder builder1 = new WebhookMessageBuilder();
            UserPhoneUser userPhoneUser = Data.userUserPhoneUserHashMap.get(event.getAuthor());
            builder1.setUsername(userPhoneUser.getUserPhoneUserName());
            builder1.setAvatarUrl(userPhoneUser.getGetUserPhoneUserProfilePicture());
            builder1.setContent(event.getMessage().getContentRaw());
            client.send(builder1.build());
        }

        try {
            if (SettingsData.antiRobServer.get(event.getGuild())) {
                if (event.getMessage().getContentRaw().startsWith("pls rob") || event.getMessage().getContentRaw().startsWith(prefix + "rob")) {
                    EmbedBuilder messageBuilder = new EmbedBuilder().setTitle("Warning!!!").setThumbnail(event.getAuthor().getAvatarUrl()).setColor(Color.RED);
                    String a = "false";

                    if (event.getAuthor().isBot()) {
                        a = "true";
                    }

                    String name, id, dis, nickname, status, statusEmoji, game, join, register;

                    User user = event.getAuthor();
                    Member member = event.getMember();

                    /* Identity */
                    name = user.getName();
                    id = user.getId();
                    dis = user.getDiscriminator();
                    nickname = member == null || member.getNickname() == null ? "N/A" : member.getEffectiveName();

                    /* Status */
                    OnlineStatus stat = member == null ? null : member.getOnlineStatus();
                    status = stat == null ? "N?A" : UtilString.VariableToString("_", stat.getKey());
                    statusEmoji = stat == null ? "" : getStatusEmoji(stat);
                    game = stat == null ? "N/A" : member.getActivities().isEmpty() ? "N/A" : member.getActivities().get(0).getName();

                    /* Time */
                    join = member == null ? "N?A" : UtilString.formatOffsetDateTime(member.getTimeJoined());
                    register = UtilString.formatOffsetDateTime(user.getTimeCreated());

                    messageBuilder.addField("***ALERT*** Someone is try to rob!!!!", "Details below.", false);

                    messageBuilder.addField("Identity", "ID `" + id + "`\n" +
                            "Nickname `" + nickname + "` | Discriminator `" + dis + "`", false);

                    messageBuilder.addField("Status", "🎮 " + " `" + game + "`\n"
                            + statusEmoji + " `" + status + "`\n", true);
                    messageBuilder.addField("Is bot?", a, true);

                    messageBuilder.addField("⌚ " + "Time", "Join - \n`" + join + "`\n" +
                            "Register `" + register + "`\n", true);
                    messageBuilder.addField("Name", name, false);
                    messageBuilder.addField("Message", event.getMessage().getContentRaw(), true);


                    event.getChannel().sendMessage(messageBuilder.build()).queue();
                    return;
                }
            }
        } catch (Exception ignored) {}

        if (event.getMessage().getContentRaw().equals(prefix + "commands")) {
            if (event.getAuthor().getId().equals(Config.get("owner_id"))) {
                em = new EmbedBuilder().setTitle("Command Count details!!!!").setColor(Color.red).setFooter("Commands used until now ").setTimestamp(LocalDateTime.now());
                em.addField("Command made by ", event.getAuthor().getName(), false);
                em.addField("Date", LocalDateTime.now().getMonthValue() + "/" + LocalDateTime.now().getDayOfMonth() + "/" + LocalDateTime.now().getYear(), false);
                em.addField("Total number of Commands used in this session....", CommandManager.commandNames.size() + " commands", false);
                em.addField("List of Commands used in this session....", commandsCount(), false);
                event.getAuthor().openPrivateChannel().complete().sendMessage(em.build()).queue();
            }
        }

        try {
            if (SettingsData.pingForPrefix.get(event.getAuthor())) {
                if (event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser())) {
                    event.getChannel().sendMessage("Psst. Check your **DMS** for the prefix of this bot").queue();
                    event.getMessage().getAuthor().openPrivateChannel().complete().sendMessage("The prefix for this bot is `" + prefix + "`").queue();
                }
            }
        } catch (Exception e) {
            SettingsData.pingForPrefix.put(event.getAuthor(), true);
        }

        jda = event.getJDA();

        System.out.println(prefix);
        if (raw.equalsIgnoreCase(prefix + "shutdown") && event.getAuthor().getId().equals(Config.get("owner_id"))) {
            shutdown(event, true);
            return;
        } else if (raw.equalsIgnoreCase(prefix + "shutdown") && event.getAuthor().getId().equals(Config.get("owner_id_partner"))) {
            shutdown(event, false);
            return;
        }

        if (raw.startsWith(prefix)) {
            try {
                manager.handle(event, prefix);
            } catch (InterruptedException | IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String commandsCount() {
        int x = 0;
        int size = CommandManager.commandNames.size();
        StringBuilder result = new StringBuilder();

        while (x < size) {
            String commandName = CommandManager.commandNames.get(x);
            result.append(x+1).append(".) ").append(commandName).append(" - ").append(count.get(commandName)).append("\n");
            x++;
        }

        return String.valueOf(result);
    }

    public static void shutdown(GuildMessageReceivedEvent event, boolean isOwner) {
        LOGGER.info("The bot " + event.getAuthor().getAsMention() + " is shutting down.\n" +
                "Thank you for using General_Hello's Code!!!");

        event.getChannel().sendMessage("Shutting down...").queue();
        event.getChannel().sendMessage("Bot successfully shutdown!").queue();
        EmbedBuilder em = new EmbedBuilder().setTitle("Shutdown details!").setColor(Color.red).setFooter("Shutdown on ").setTimestamp(LocalDateTime.now());
        em.addField("Shutdown made by ", event.getAuthor().getName(), false);
        em.addField("Date", LocalDateTime.now().getMonthValue() + "/" + LocalDateTime.now().getDayOfMonth() + "/" + LocalDateTime.now().getYear(), false);
        em.addField("Total number of Commands used during this session....", CommandManager.commandNames.size() + " commands", false);
        em.addField("List of Commands used during this session....", commandsCount(), false);
        event.getAuthor().openPrivateChannel().complete().sendMessage(em.build()).queue();

        if (!isOwner) {
            User owner = event.getJDA().retrieveUserById(Config.get("owner_id")).complete();
            owner.openPrivateChannel().complete().sendMessage(em.build()).queue();
        }


        event.getJDA().shutdown();
        SQLiteDataSource.ds.close();
        BotCommons.shutdown(event.getJDA());
    }
}
