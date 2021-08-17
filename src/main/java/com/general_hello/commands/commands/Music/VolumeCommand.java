package com.general_hello.commands.commands.Music;

import com.general_hello.commands.SlashCommands.SlashCommand;
import com.general_hello.commands.SlashCommands.SlashCommandContext;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VolumeCommand extends SlashCommand
{
    public VolumeCommand()
    {
        setCommandData(new CommandData("volume", "change the volume of the bot")
                .addOption(OptionType.INTEGER, "volume", "the volume (1-100)", true)
        );
    }

    @Override
    public void executeCommand(@NotNull SlashCommandEvent event, @Nullable Member sender, @NotNull SlashCommandContext ctx)
    {
        Member member = event.getMember();
        OptionMapping option = event.getOption("volume");
        int volume = (int) Math.max(1, Math.min(100, option.getAsLong()));
        GuildAudioPlayer guildAudioPlayer = AudioManager.getAudioPlayer(event.getGuild().getIdLong());
        guildAudioPlayer.getPlayer().setVolume(volume);
        ctx.sendSimpleEmbed("The volume has been adjusted to `" + volume + "%`!");
    }

}