package com.general_hello.commands.SlashCommands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnSlashCommand extends ListenerAdapter
{
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event)
    {
        SlashCommandHandler.handleSlashCommand(event, event.getMember());
    }
}