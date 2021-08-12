package com.general_hello.commands.commands.RankingSystem;

import com.general_hello.commands.SlashCommands.SlashCommand;
import com.general_hello.commands.SlashCommands.SlashCommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;

public class ViewRankSlashCommand extends SlashCommand
{
    public ViewRankSlashCommand()
    {

        setCommandData(new CommandData("rank", "Sends your rank").addOption(OptionType.USER, "member", "user to check the rank")
        );
        setRunnableInDM(false);
    }

    @Override
    public void executeCommand(@NotNull SlashCommandEvent event, @Nullable Member sender, @NotNull SlashCommandContext ctx) throws SQLException {
        try{
            ByteArrayOutputStream baos = LevelPointManager.getLevelPointCard(event.getMember()).getByteArrayOutputStream();
            event.getChannel().sendFile(baos.toByteArray(), "stats.png").queue();
        }
        catch(Exception e){
            event.getChannel().sendMessage("\u274C").queue();
        }
    }
}
