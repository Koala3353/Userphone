package com.general_hello.commands.commands.Music;

import com.general_hello.commands.SlashCommands.SlashCommand;
import com.general_hello.commands.SlashCommands.SlashCommandContext;
import com.general_hello.commands.commands.Emoji.Emoji;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PauseCommand extends SlashCommand
{
    public PauseCommand()
    {
        setCommandData(new CommandData("pause", "pause the currently playing song"));
    }

    @Override
    public void executeCommand(@NotNull SlashCommandEvent event, @Nullable Member sender, @NotNull SlashCommandContext ctx)
    {
        GuildAudioPlayer guildAudioPlayer = AudioManager.getAudioPlayer(event.getGuild().getIdLong());
        AudioPlayer player = guildAudioPlayer.getPlayer();
        if (player.getPlayingTrack() == null)
        {
            ctx.replyError("I'm currently not playing any music!").queue();
        }
        if (player.isPaused())
        {
            ctx.replyError("The player is already paused! Use `/resume`").queue();
            return;
        }
        player.setPaused(true);
        ctx.sendSimpleEmbed("The player is now paused! " + Emoji.USER);
    }
}