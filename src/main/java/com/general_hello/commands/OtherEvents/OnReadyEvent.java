package com.general_hello.commands.OtherEvents;

import com.general_hello.commands.Bot;
import com.general_hello.commands.SlashCommands.SlashCommandHandler;
import com.general_hello.commands.commands.Utils.TextUploader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.OffsetDateTime;

public class OnReadyEvent extends ListenerAdapter {
    public static Config config = ConfigFactory.load();
    public static TextUploader uploader;

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Bot.jda = event.getJDA();
        new SlashCommandHandler();
        SlashCommandHandler.initialize();
        uploader = new TextUploader(config.getStringList("upload-webhooks"));
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Status").setColor(Color.GREEN).setTimestamp(OffsetDateTime.now()).setDescription(event.getJDA().getSelfUser().getAsMention() + " is now online! The problem has been resolved and the maintenance is complete!");
        event.getJDA().getTextChannelById(852342009288851516L).sendMessageEmbeds(embedBuilder.build()).queue();
    }
}
