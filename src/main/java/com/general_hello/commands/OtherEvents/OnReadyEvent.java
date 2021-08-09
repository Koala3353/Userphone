package com.general_hello.commands.OtherEvents;

import com.general_hello.commands.Bot;
import com.general_hello.commands.SlashCommands.SlashCommandHandler;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnReadyEvent extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Bot.jda = event.getJDA();
        SlashCommandHandler slashCommandHandler = new SlashCommandHandler();
        SlashCommandHandler.initialize();
    }
}
