package com.general_hello.commands.commands.Register;

import com.general_hello.commands.SlashCommands.SlashCommand;
import com.general_hello.commands.commands.GetData;
import com.general_hello.commands.commands.Info.InfoUserCommand;
import com.general_hello.commands.commands.RankingSystem.SlashCommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class RegisterSlashCommand extends SlashCommand {
    public RegisterSlashCommand()
    {
        setCommandData(new CommandData("register", "Registers a user to the database"));
        setRunnableInDM(false);
    }

    @Override
    public void executeCommand(@NotNull SlashCommandEvent event, @Nullable Member sender, @NotNull SlashCommandContext ctx) throws SQLException {
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
    }
}
