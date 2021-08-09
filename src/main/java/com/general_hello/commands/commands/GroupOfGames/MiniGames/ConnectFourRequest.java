package com.general_hello.commands.commands.GroupOfGames.MiniGames;

import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.Emoji.Emoji;
import com.general_hello.commands.commands.ICommand;
import com.general_hello.commands.commands.Register.Data;
import com.general_hello.commands.commands.User.UserPhoneUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.io.IOException;
import java.time.OffsetDateTime;

public class ConnectFourRequest implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws InterruptedException, IOException {
        if (ctx.getMessage().getMentionedUsers().isEmpty()) {
            ctx.getChannel().sendMessage(Emoji.ERROR + " Kindly mention the user you want to play with.").queue();
            return;
        }
        User mentionedUser = ctx.getMessage().getMentionedUsers().get(0);

        UserPhoneUser userPhoneUser = Data.userUserPhoneUserHashMap.get(ctx.getAuthor());
        //Main code
        //Build the embed
        EmbedBuilder embedBuilder = new EmbedBuilder().setTitle("Request to Play Connect4").setFooter("Click on accept if you accept the challenge!");
        embedBuilder.setDescription(ctx.getMember().getEffectiveName() + " is challenging you to a game of Connect Four!\n" +
                "\n**Challenger's Profile:**" +
                "\n" +

                "Userphone Name: *" + userPhoneUser.getUserPhoneUserName() + "*")
                .setTimestamp(OffsetDateTime.now()).setColor(Color.YELLOW)
        .setThumbnail("https://cdn.game.tv/game-tv-content/images_3/fb18d4991b4f4112deb57f206a857829/AppIcons.jpg");

        ctx.getChannel().sendMessage(mentionedUser.getAsMention()).queue();
        String messageID = ctx.getChannel().sendMessageEmbeds(embedBuilder.build()).setActionRow(Button.primary(ctx.getMember().getId() + ":acceptConnect4", "Accept")).complete().getId();
        String messageLink = "https://discord.com/channels/" + ctx.getGuild().getId() + "/" + ctx.getChannel().getId() + "/" + messageID;
        //DM the requested user for the channel link
        mentionedUser.openPrivateChannel().queue((privateChannel -> {
            embedBuilder.clear();
            embedBuilder.setTitle("Request to Play Connect4");
            embedBuilder.setColor(Color.YELLOW);
            embedBuilder.setDescription(ctx.getAuthor().getName() + " is challenging you a game of Connect4 in " + ctx.getGuild().getName() + " at " + ctx.getChannel().getAsMention() + " in this [message](" + messageLink + ")!");
            privateChannel.sendMessageEmbeds(embedBuilder.build()).queue();
        }));
    }

    @Override
    public String getName() {
        return "connect4";
    }

    @Override
    public String getHelp(String prefix) {
        return "Plays connect4 with a user!\n" +
                "Usage: `" + prefix + getName() + "[mentioned user]";
    }
}
