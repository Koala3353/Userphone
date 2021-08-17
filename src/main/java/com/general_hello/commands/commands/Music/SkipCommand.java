package com.general_hello.commands.commands.Music;

import com.general_hello.commands.SlashCommands.SlashCommand;
import com.general_hello.commands.SlashCommands.SlashCommandContext;
import com.general_hello.commands.commands.Emoji.Emoji;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkipCommand extends SlashCommand
{
    public SkipCommand()
    {
        setCommandData(new CommandData("skip", "skips the currently playing track"));
    }

    @Override
    public void executeCommand(@NotNull SlashCommandEvent event, @Nullable Member sender, @NotNull SlashCommandContext ctx)
    {
        GuildAudioPlayer guildAudioPlayer = AudioManager.getAudioPlayer(event.getGuild().getIdLong());
        if (guildAudioPlayer.getScheduler().isRepeat())
        {
            guildAudioPlayer.getScheduler().setRepeat(false);
        }
        if (guildAudioPlayer.getPlayer().getPlayingTrack() == null)
        {
            ctx.sendSimpleEmbed(Emoji.ERROR + " There is no music to skip!");
            return;
        }
        guildAudioPlayer.getScheduler().nextTrack();
        AudioTrack nextTrack = guildAudioPlayer.getPlayer().getPlayingTrack();
        if (nextTrack == null)
        {
            ctx.sendSimpleEmbed("**Skipped!**");
            return;
        }
        ctx.sendSimpleEmbed("**Skipped!** Now playing: `" + nextTrack.getInfo().title + "`");
    }
}
