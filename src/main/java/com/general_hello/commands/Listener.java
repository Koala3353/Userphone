package com.general_hello.commands;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.general_hello.commands.Database.DatabaseManager;
import com.general_hello.commands.Database.SQLiteDataSource;
import com.general_hello.commands.commands.Call.QueueDatabase;
import com.general_hello.commands.commands.GetData;
import com.general_hello.commands.commands.ICommand;
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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.general_hello.commands.commands.Utils.UtilBot.getStatusEmoji;

public class Listener extends ListenerAdapter {
    private final CommandManager manager;
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    public static HashMap<String, Integer> count = new HashMap<>();
    private static OffsetDateTime timeDisconnected = OffsetDateTime.now();
    public static JDA jda;
    public static int disconnectCount = 0;

    public Listener(EventWaiter waiter) {
        manager = new CommandManager(waiter);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onReconnected(@NotNull ReconnectedEvent event) {
        LOGGER.info("{} is reconnected!! Response number {}", event.getJDA().getSelfUser().getAsTag(), event.getResponseNumber());
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        final String useGuildSpecificSettingsInstead = String.format("Thank you for adding %s to %s!!!\n" +
                        "\nTo learn more about this bot feel free to type **u?about**\n" +
                        "You can change the prefix by typing **u?setprefix [prefix]**\n" +
                        "To learn more about a command **u?help [command name]**",
                event.getJDA().getSelfUser().getAsMention(), event.getGuild().getName());

        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Hello!").setDescription(useGuildSpecificSettingsInstead);
        event.getJDA().getUserById(Config.get("owner_id")).openPrivateChannel().complete().sendMessage("Someone added me to " + event.getGuild().getName()).queue();
        event.getJDA().getUserById(Config.get("owner_id")).openPrivateChannel().complete().sendMessage("Invite link is " + event.getGuild().retrieveInvites().complete().get(0).getUrl()).queue();
        SettingsData.antiRobServer.put(event.getGuild(), false);
        embedBuilder.setColor(InfoUserCommand.randomColor());
        event.getGuild().getOwner().getUser().openPrivateChannel().complete().sendMessage(embedBuilder.build()).queue();
        try {
            event.getGuild().getTextChannels().get(0).sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            try {
                event.getGuild().getTextChannels().get(1).sendMessage(embedBuilder.build()).queue();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onResumed(@NotNull ResumedEvent event)  {
        TextChannel guildChannelById = event.getJDA().getTextChannelById(852338750519640116L);
        EmbedBuilder em = new EmbedBuilder().setColor(Color.RED).setTitle("🔴 Disconnected");
        disconnectCount++;
        em.setDescription("The bot disconnected for " +
                (OffsetDateTime.now().getHour() - timeDisconnected.getHour())  + " hour(s) " +
                (OffsetDateTime.now().getMinute() - timeDisconnected.getMinute()) + " minute(s) " +
                (OffsetDateTime.now().getSecond() - timeDisconnected.getSecond()) + " second(s) and " +
                (timeDisconnected.getNano() /1000000) + " milliseconds due to connectivity issues!\n" +
                "Response number: " + event.getResponseNumber()).setTimestamp(OffsetDateTime.now()).setFooter("The bot disconnected " + disconnectCount + " times already since the last restart!");
        guildChannelById.sendMessage(em.build()).queue();
        User owner_id = event.getJDA().getUserById(Config.get("owner_id"));
        owner_id.openPrivateChannel().complete().sendMessage(em.build()).queue();
    }

    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        event.deferEdit().queue();

        switch (event.getSelectedOptions().get(0).getValue()) {
            case "reject":
                event.getUser().openPrivateChannel().complete().sendMessage("Sorry, you are too young to use this bot! (You shouldn't be on Discord!)").queue();
                event.getMessage().delete().queue();
                return;
            case "noice":
            case "oh":
            case "old":
                EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Rules").setColor(InfoUserCommand.randomColor());
                String arrow = "<a:arrow_1:862525611465113640>";
                String message = arrow + " Be respectful to everyone. Do not be disruptive, rude, vulgar or otherwise act inappropriately towards other members in the call.\n" +
                        "\n" +
                        arrow + " Hate speech, including speech that is racist, sexist, or derogatory speech based on sexual orientation, are not allowed. No personal attacks are allowed.\n\n" +
                        arrow + " Having multiple accounts on the call/server is discouraged for all members.\n\n" +
                        arrow + " This is a family friendly server. All content, including images, names, and text shall be appropriate for all ages.\n\n" +
                        arrow + " No trolling and/or spamming.\n\n" +
                        arrow + " Discussions on conduct that violates the Terms of Service of Discord and Bots are not allowed. This includes but not limited to cheating, hacking, botting, and currency selling, as well as all other prohibited conduct.\n\n" +
                        arrow + " Members are not allowed in spamming referral codes or discord invitations, self-promotion, or advertising.\n\n" +
                        arrow + " No trading or begging. Links, when used for discussion, are allowed.\n\n" +
                        arrow + " Personal information includes but not limited to, pictures of members, may not be posted at any time for any reason.\n\n" +
                        arrow + " Information about this bot is unofficial unless provided directly by an employee of the bot. Server Moderators and Admins are not employees of the bot.\n\n" +
                        arrow + " [Discord's Partnership Code of Conduct](https://support.discordapp.com/hc/en-us/articles/360024871991-Discord-Partnership-Code-of-Conduct) and [Terms of Service](https://discordapp.com/terms) must be adhered to.";

                embedBuilder.setDescription(message);
                embedBuilder.setFooter("Press the Accept button if you accept the rules stated above!");

                String id = event.getUser().openPrivateChannel().complete().sendMessageEmbeds(embedBuilder.build()).setActionRow(
                        Button.primary("0000:accept", "Accept").withEmoji(Emoji.fromEmote("verify", Long.parseLong("803768813110951947"), true))
                ).complete().getPrivateChannel().getId();
                event.getMessage().delete().queue();
                return;
            case "n/a": 
            default:
                event.deferEdit().queue();
        }
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(InfoUserCommand.randomColor());
        if (Data.progress.containsKey(event.getAuthor())) {
            Integer progress = Data.progress.get(event.getAuthor());
            if (progress == 1) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(event.getMessage().getContentRaw());
                Data.answers.put(event.getAuthor(), arrayList);
                Data.progress.put(event.getAuthor(), 2);
                event.getChannel().sendMessage("Hello **" + event.getMessage().getContentRaw() + "**! What do you want your profile picture to be? (LINK ONLY)").queue();
                return;
            }

            ArrayList<String> oldAnswers = Data.answers.get(event.getAuthor());

            if (progress == 2) {
                embedBuilder.setTitle("Profile Picture");
                try {
                    embedBuilder.setImage(event.getMessage().getContentRaw());
                } catch (Exception e) {
                    event.getChannel().sendMessage("The link provided is invalid! Kindly try a different one!").queue();
                    return;
                }
                oldAnswers.add(event.getMessage().getContentRaw());
                Data.answers.put(event.getAuthor(), oldAnswers);
                Data.progress.put(event.getAuthor(), 100);

                embedBuilder.setDescription("[This](" + event.getMessage().getContentRaw() + ") image will be the profile picture that will be used with **Userphone**");
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                embedBuilder.clear();
                embedBuilder.setTitle("Account Summary");
                embedBuilder.setThumbnail(event.getMessage().getContentRaw());
                embedBuilder.setDescription("Name: ***" + oldAnswers.get(0) + "***\n" +
                        "Profile Picture: [Here](" + oldAnswers.get(1) + ")\n" +
                        "Agreed to all rules: ✅").setFooter("Thank you for registering!");
                embedBuilder.setColor(InfoUserCommand.randomColor());
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

                UserPhoneUser user = new UserPhoneUser(oldAnswers.get(0), event.getAuthor(), oldAnswers.get(1));
                Data.userUserPhoneUserHashMap.put(event.getAuthor(), user);
                Data.userPhoneUsers.add(user);

                System.out.println(oldAnswers.get(0) + "-" + oldAnswers.get(1));

                DatabaseManager.INSTANCE.newInfo(event.getAuthor().getIdLong(), oldAnswers.get(0), oldAnswers.get(1));
            }
        }
    }

    @Override
    public void onDisconnect(@NotNull DisconnectEvent event) {
        timeDisconnected = event.getTimeDisconnected();
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        System.out.println("Shut downed the bot at " +
                event.getTimeShutdown().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+ " due to maintenance.\n" +
                "With response number of " + event.getResponseNumber() + "\n" +
                "With the code of " + event.getCloseCode().getCode() + "\n");
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        // Only accept commands from guilds
        final long guildID = event.getGuild().getIdLong();
        String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, (id) -> Config.get("prefix"));

        try {
            switch (event.getName()) {
                case "register":
                    GetData getData = new GetData();
                    getData.checkIfContainsData(event.getUser(), event);
                    if (Data.userUserPhoneUserHashMap.containsKey(event.getUser())) {
                        event.reply("You are already registered!").setEphemeral(true).queue();
                        return;
                    }

                    SelectionMenu menu = SelectionMenu.create("menu:class")
                            .setPlaceholder("Choose your age") // shows the placeholder indicating what this menu is for
                            .setRequiredRange(1, 1) // only one can be selected
                            .addOption("< 13", "reject")
                            .addOption("13 - 21", "noice")
                            .addOption("22 - 40", "oh")
                            .addOption("41 +", "old")
                            .addOption("Prefer not to say", "n/a")
                            .build();

                    EmbedBuilder embedBuilder = new EmbedBuilder().setColor(InfoUserCommand.randomColor()).setTitle("Age");
                    embedBuilder.setDescription("How old are you? Select your answer below.");
                    String id = event.getUser().openPrivateChannel().complete().sendMessageEmbeds(embedBuilder.build())
                            .setActionRow(menu)
                            .complete().getPrivateChannel().getId();

                    String privateChannelLink = "https://discord.com/channels/@me/" + id;
                    privateChannelLink = "[DM](" + privateChannelLink + ")";
                    embedBuilder = new EmbedBuilder().setTitle("Register").setColor(InfoUserCommand.randomColor());
                    embedBuilder.setDescription("Let's go to your " + privateChannelLink + " to continue shall we?");
                    event.replyEmbeds(embedBuilder.build()).queue();
                    break;
                case "connect4":
                    OptionMapping userSelected = event.getOption("user");
                    User user = userSelected.getAsUser();

                    //TODO: Get from CLASS of ConnectFourRequest.java
                    break;
                case "help":
                    OptionMapping commandName = event.getOption("command");

                    embedBuilder = new EmbedBuilder();

                    if (commandName == null) {
                        embedBuilder.setTitle("Groups");
                        embedBuilder.setColor(Color.cyan);
                        embedBuilder.addField(com.general_hello.commands.commands.Emoji.Emoji.USER + " | User (1)", "Shows the basics of complex commands you can do with the bot", false);
                        embedBuilder.addField(com.general_hello.commands.commands.Emoji.Emoji.DISCORD_BOT + " | Bot (8)", "Shows the commands you can do with the bot", false);
                        embedBuilder.addField(com.general_hello.commands.commands.Emoji.Emoji.INFO + " | Info (3)", "Shows basic to complex information about users, mods, or servers", false);
                        embedBuilder.addField(com.general_hello.commands.commands.Emoji.Emoji.MOD + " | Moderation (0)", "Moderation tools used by staff to control or monitor the server.", false);

                        embedBuilder.setFooter("Type " + prefix + "help [group name] to see their commands");

                        boolean disableOrEnable = true;

                        if (event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                            disableOrEnable = false;
                        }

                        event.replyEmbeds(embedBuilder.build()).addActionRow(
                                Button.secondary(event.getMember().getUser().getId() + ":user", "User").withEmoji(Emoji.fromEmote("user", Long.parseLong("862895295239028756"), true)),
                                Button.secondary(event.getMember().getUser().getId() + ":bot", "Bot").withEmoji(Emoji.fromEmote("discord_bot", Long.parseLong("862895574960701440"), false)),
                                Button.secondary(event.getMember().getUser().getId() + ":info", "Info").withEmoji(Emoji.fromEmote("info", Long.parseLong("870871190217060393"), true)),
                                Button.secondary(event.getMember().getUser().getId() + ":mod", "Moderation").withDisabled(disableOrEnable).withEmoji(Emoji.fromEmote("mod", Long.parseLong("862898484041482270"), true)),
                                Button.danger(event.getMember().getUser().getId() + ":end", "Cancel").withDisabled(disableOrEnable).withEmoji(Emoji.fromEmote("cancel", Long.parseLong("863204248657461298"), true))
                        ).queue();
                    } else {

                        ICommand command = manager.getCommand(Bot.longToCommandName.get(commandName.getAsLong()));

                        if (command == null) {
                            event.reply("Nothing found for " + commandName).setEphemeral(true).queue();
                            return;
                        }

                        embedBuilder.setTitle("Help");
                        embedBuilder.setColor(InfoUserCommand.randomColor());
                        embedBuilder.setDescription(command.getHelp(prefix));
                        embedBuilder.setTimestamp(OffsetDateTime.now());
                        event.replyEmbeds(embedBuilder.build()).queue();
                    }
                    break;
                default:
                    event.reply("Sorry, I can't handle that command right now :(").setEphemeral(true).queue();
            }
        } catch(Exception exception){
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        // users can spoof this id so be careful what you do with this
        String[] id = event.getComponentId().split(":"); // this is the custom id we specified in our button
        String authorId = id[0];
        String type = id[1];
        // When storing state like this is it is highly recommended to do some kind of verification that it was generated by you, for instance a signature or local cache
        event.deferEdit().queue(); // acknowledge the button was clicked, otherwise the interaction will fail

        if (!authorId.equals("0000") && !authorId.equals(event.getUser().getId()))
            return;

        switch (type)
        {
            case "kick":
                event.getHook().editOriginal("Bye 😫").queue();
                event.getGuild().leave().queue();
                // fallthrough delete the prompt message with our buttons
                break;
            case "nope":
            case "end":
                event.getHook().deleteOriginal().queue();
                break;
            case "user":
                event.getHook().editOriginalEmbeds(helpCrap(4, event).build()).queue();
                break;
            case "bot":
                event.getHook().editOriginalEmbeds(helpCrap(2, event).build()).queue();
                break;
            case "info":
                event.getHook().editOriginalEmbeds(helpCrap(1, event).build()).queue();
                break;
            case "mod":
                event.getHook().editOriginalEmbeds(helpCrap(3, event).build()).queue();
                break;
            case "accept":
                String arrow = "<a:arrow_1:862525611465113640>";

                event.getHook().deleteOriginal().queue();
                EmbedBuilder em = new EmbedBuilder().setTitle("Stored data").setFooter("Press the Accept button if you accept the data that will be stored!\n");
                em.setDescription("The bot stores the following data:\n" +
                        arrow + " Reads all sent messages in the server the bot is in.\n" +
                        arrow + " Reads all the messages you sent to the bot.\n" +
                        arrow + " Reads all the calls you made.\n" +
                        arrow + " Reads your user name, profile picture, nitro status, and user id.\n" +
                        arrow + " Reads all the permissions you have on that server." +
                        arrow + " Listens to everything you say during voicecalls.\n");
                event.getChannel().sendMessageEmbeds(em.build()).setActionRow(
                        Button.primary("0000:yes", "Accept").withEmoji(Emoji.fromEmote("verify", Long.parseLong("803768813110951947"), true))
                ).queue();
                break;
            case "yes":
                event.getHook().deleteOriginal().queue();
                event.getChannel().sendMessage("<a:thanks:863989523461177394> Thank you for accepting the rules and data that will be stored.").queue();
                event.getChannel().sendMessage("<a:question:863989523368247346> What do you want your username for calls to be?").queue();
                Data.progress.put(event.getUser(), 1);
                break;
            case "acceptConnect4":
                final long guildID = event.getGuild().getIdLong();
                String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, DatabaseManager.INSTANCE::getPrefix);
                List<Category> connect4Category = event.getGuild().getCategoriesByName("Connect4", false);
                if (connect4Category.isEmpty()){
                    event.getGuild().createCategory("Connect4").setPosition(1).queue();
                }

                connect4Category.get(0).createTextChannel("Connect").setPosition(1).setTopic("Connect4 games with your friend!").queue();
                event.getChannel().sendMessage(event.getMember().getAsMention() + " accepted the Connect4 game! Kindly navigate to " + " and send " + prefix + "startconnect4 once you are ready!").queue();
                break;
        }
    }

    public EmbedBuilder helpCrap (int number, ButtonClickEvent ctx) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        final long guildID = ctx.getGuild().getIdLong();
        String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, (id) -> Config.get("prefix"));

        switch (number) {
            case 1:
                embedBuilder.setTitle("Information Commands");
                embedBuilder.setColor(Color.red);
                embedBuilder.addField("1.) Profile Command","`" + prefix + "profile`", false);
                embedBuilder.addField("2.) Server Information Command","`" + prefix + "serverinfo`", false);
                embedBuilder.addField("3.) Mod Information Command ","`" + prefix + "mods`", false);

                embedBuilder.setFooter("\nType " + prefix + "help [command name] to see what they do");
                break;
            case 2:
                embedBuilder.setTitle("About the Bot Commands");
                embedBuilder.setColor(Color.blue);
                embedBuilder.addField("1.) Server List Command","`" + prefix + "serverlist`", false);
                embedBuilder.addField("2.) Server Count Command","`" + prefix + "server`", false);
                embedBuilder.addField("3.) Bug Command","`" + prefix + "bug`", false);
                embedBuilder.addField("4.) Setting Command ","`" + prefix + "settings`", false);
                embedBuilder.addField("5.) Upgrade to ***Premium*** Command ","`" + prefix + "premium`", false);
                embedBuilder.addField("6.) Set Prefix Command", "`" + prefix + "setprefix`", false);
                embedBuilder.addField("7.) Use Code Command", "`" + prefix + "code`", false);
                embedBuilder.addField("8.) Uptime Command", "`" + prefix + "uptime`", false);

                embedBuilder.setFooter("\nType " + prefix + "help [command name] to see what they do");
                break;
            case 3:
                embedBuilder.setTitle("Moderation Commands");
                embedBuilder.setColor(Color.red);
                embedBuilder.addField("1.) Kick Command", "`" + prefix + "kick`", false);
                embedBuilder.addField("2.) Ban Command", "`" + prefix + "ban`", false);
                embedBuilder.addField("3.) Mute Command", "`" + prefix + "mute`", false);
                embedBuilder.addField("4.) Deafen Command", "`" + prefix + "deafen`", false);
                embedBuilder.setFooter("\nType " + prefix + "help [command name] to see what they do");
                break;
            case 4:
                embedBuilder.setTitle("User Commands");
                embedBuilder.setColor(Color.red);
                embedBuilder.addField("1.) Register Command", "`" + prefix + "register`", false);

                embedBuilder.setFooter("\nType " + prefix + "help [command name] to see what they do");
                break;
        }
        return embedBuilder;
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        GetData getData = new GetData();
        getData.checkIfContainsData(event.getAuthor(), event);

        EmbedBuilder em = new EmbedBuilder().setColor(Color.YELLOW).setAuthor("Message from: " + event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
        String inviteLink;

        try {
            inviteLink = event.getGuild().retrieveInvites().complete().get(0).getUrl();
        } catch (Exception e) {
            inviteLink = "https://discord.com/404";
        }

        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        if (event.getChannel() != event.getJDA().getTextChannelById("854187935104237599")) {
            em.setDescription("Message: " + event.getMessage().getContentRaw() + "\n" +
                    "Sent in the channel ***" +
                    event.getChannel().getName() + "*** in ***" +
                    event.getGuild().getName() + "***\n" +
                    "Invite link of the server [here](" + inviteLink + ")");

            em.setFooter(event.getGuild().getVoiceChannels().get(0).getRegion().getEmoji());
            event.getJDA().getTextChannelById("854187935104237599").sendMessageEmbeds(em.build()).queue();
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
