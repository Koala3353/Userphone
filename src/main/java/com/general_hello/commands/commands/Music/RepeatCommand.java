package com.general_hello.commands.commands.Music;

import com.general_hello.commands.SlashCommands.SlashCommand;
import com.general_hello.commands.SlashCommands.SlashCommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RepeatCommand extends SlashCommand
{
    public RepeatCommand()
    {
        setCommandData(new CommandData("repeat", "repeats the currently playing song"));
    }

    @Override
    public void executeCommand(@NotNull SlashCommandEvent event, @Nullable Member sender, @NotNull SlashCommandContext ctx)
    {
        GuildAudioPlayer player = AudioManager.getAudioPlayer(event.getGuild().getIdLong());
        if (player.getPlayer().getPlayingTrack() == null)
        {
            ctx.replyError("I'm currently not playing any music!").queue();
            return;
        }
        if (!player.getScheduler().isRepeat())
        {
            player.getScheduler().setRepeat(true);
            ctx.sendSimpleEmbed("\uD83D\uDD01 Repeat mode turned **ON**\nUse this command again to turn it off.");
        } else
        {
            player.getScheduler().setRepeat(false);
            ctx.sendSimpleEmbed("\uD83D\uDD01 Repeat mode turned **OFF**");
        }
    }
}
