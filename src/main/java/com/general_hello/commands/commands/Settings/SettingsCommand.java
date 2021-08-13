package com.general_hello.commands.commands.Settings;

import com.general_hello.commands.Config;
import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws InterruptedException, IOException {
            EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(ctx.getAuthor().getName() + "'s settings for " + ctx.getSelfUser().getName()).setFooter("Your settings ↔ Home Page").setColor(Color.ORANGE);
            embedBuilder.setDescription("Kindly select the category you want to change the setting of. For example, Pressing/Clicking on the button ***User*** will bring you to a setting page where you can set your user setting for `Userphone Bot`");

            boolean isMod = true;

            if (ctx.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                isMod = false;
            }

            boolean isOwner = true;

            if (ctx.getAuthor().getId().equals(Config.get("owner_id")) || ctx.getAuthor().getId().equals(Config.get("owner_id_partner"))) {
                isOwner = false;
            }

            ctx.getChannel().sendMessageEmbeds(embedBuilder.build()).setActionRow(
                    Button.primary(ctx.getAuthor().getId() + ":usersetting", "User").withEmoji(net.dv8tion.jda.api.entities.Emoji.fromEmote("user", 862895295239028756L, true)),
                    Button.primary(ctx.getAuthor().getId() + ":modsetting", "Moderation").withDisabled(isMod).withEmoji(net.dv8tion.jda.api.entities.Emoji.fromEmote("mod", 862898484041482270L, true)),
                    Button.primary(ctx.getAuthor().getId() + ":ownersetting", "Owner").withDisabled(isOwner).withEmoji(net.dv8tion.jda.api.entities.Emoji.fromEmote("babyyoda", 866105061665669140L, true))
            ).queue();
    }

    @Override
    public String getName() {
        return "setting";
    }

    @Override
    public List<String> getAliases() {
        List<String> list = new ArrayList<>();
        list.add("settings");
        list.add("serversettings");
        return list;
    }

    @Override
    public String getHelp(String prefix) {
        return "Changes the setting of the bot to the user or to the server\n" +
                "Usage: `" + prefix + getName() + "`";
    }
}
