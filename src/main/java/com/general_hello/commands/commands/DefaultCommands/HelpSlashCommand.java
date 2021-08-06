package com.general_hello.commands.commands.DefaultCommands;

import com.general_hello.commands.*;
import com.general_hello.commands.SlashCommands.SlashCommand;
import com.general_hello.commands.commands.ICommand;
import com.general_hello.commands.commands.Info.InfoUserCommand;
import com.general_hello.commands.commands.PrefixStoring;
import com.general_hello.commands.commands.RankingSystem.SlashCommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class HelpSlashCommand extends SlashCommand
{
    public HelpSlashCommand()
    {
        int x = 0;

        ArrayList<SubcommandData> choices = new ArrayList<>();
        while (x < CommandManager.cmdNames.size()) {
            System.out.println(CommandManager.cmdNames.get(x) + " added to choices!");
            SubcommandData choice = new SubcommandData(CommandManager.cmdNames.get(x), String.valueOf(x+1));
            Bot.longToCommandName.put((long) (x+1), CommandManager.cmdNames.get(x));
            choices.add(choice);
            x++;
        }

        setCommandData(new CommandData("help", "Sends the help message")
                .addOptions(new OptionData(OptionType.STRING, "command", "What subcommand you want to check the help on")).addSubcommands(choices)
        );
        setRunnableInDM(false);

    }

    @Override
    public void executeCommand(@NotNull SlashCommandEvent event, @Nullable Member sender, @NotNull SlashCommandContext ctx) throws SQLException {
        final long guildID = event.getGuild().getIdLong();
        String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, (id) -> Config.get("prefix"));

        EmbedBuilder embedBuilder;
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
                    net.dv8tion.jda.api.interactions.components.Button.secondary(event.getMember().getUser().getId() + ":user", "User").withEmoji(Emoji.fromEmote("user", Long.parseLong("862895295239028756"), true)),
                    net.dv8tion.jda.api.interactions.components.Button.secondary(event.getMember().getUser().getId() + ":bot", "Bot").withEmoji(Emoji.fromEmote("discord_bot", Long.parseLong("862895574960701440"), false)),
                    net.dv8tion.jda.api.interactions.components.Button.secondary(event.getMember().getUser().getId() + ":info", "Info").withEmoji(Emoji.fromEmote("info", Long.parseLong("870871190217060393"), true)),
                    net.dv8tion.jda.api.interactions.components.Button.secondary(event.getMember().getUser().getId() + ":mod", "Moderation").withDisabled(disableOrEnable).withEmoji(Emoji.fromEmote("mod", Long.parseLong("862898484041482270"), true)),
                    Button.danger(event.getMember().getUser().getId() + ":end", "Cancel").withDisabled(disableOrEnable).withEmoji(Emoji.fromEmote("cancel", Long.parseLong("863204248657461298"), true))
            ).queue();
        } else {

            ICommand command = Listener.manager.getCommand(Bot.longToCommandName.get(commandName.getAsLong()));

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
    }
}
