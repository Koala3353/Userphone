package com.general_hello.commands.commands.Utils;

import com.general_hello.commands.Config;
import com.general_hello.commands.commands.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ErrorUtils {
    public static void error(CommandContext event, Exception e) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("An error occurred while executing a slash-command!")
                .addField("Guild", (event.getGuild() == null ? "None (Direct message)" : event.getGuild().getIdLong()+" ("+event.getGuild().getName()+")"),true)
                .addField("User", event.getAuthor().getAsMention()+" ("+event.getAuthor().getAsTag()+")", true)
                .addField("Command", event.getMessage().getContentRaw(), false)
                .setDescription("```\n"+ e.getLocalizedMessage() +"\n```")
                .setColor(Color.RED);
        event.getJDA().openPrivateChannelById(Config.get("owner_id"))
                .flatMap(c -> c.sendMessageEmbeds(builder.build()))
                .queue();

        event.getJDA().openPrivateChannelById(Config.get("owner_id_partner"))
                .flatMap(c -> c.sendMessageEmbeds(builder.build()))
                .queue();

        event.getMessage().reply("An unknown error occurred! The owner of the bot has been notified of this!").queue(s -> {}, ex -> {});
    }
}
