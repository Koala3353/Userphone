package com.general_hello.commands.OtherEvents;

import com.general_hello.commands.commands.GetData;
import com.general_hello.commands.commands.Info.InfoUserCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class OnSelectionMenu extends ListenerAdapter {
    @Override
    public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
        switch (event.getSelectedOptions().get(0).getValue()) {
            case "enableXP":
                String isEnabled = "Enabled";
                boolean isEnabledBoolean = true;
                boolean isDisabledBoolean = false;
                if (GetData.blackListedGuild.contains(event.getGuild())) {
                    isEnabled = "Disabled";
                    isEnabledBoolean = false;
                    isDisabledBoolean = true;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder().setTitle(event.getGuild().getName() + "'s settings for XP System").setFooter("Your settings ↔ XP System Setting Page").setColor(Color.ORANGE);
                embedBuilder.setDescription("XP system - Grants users experience points (XP) and levels based on their activity in a server. Its main purpose is to reward member activity in the community.\n\n" +
                        "Current setting: **" + isEnabled + "**");

                event.replyEmbeds(embedBuilder.build()).setEphemeral(true)
                        .addActionRow(
                                Button.primary(event.getUser().getId() + ":enableXPSystem", "Enable").withDisabled(isEnabledBoolean),
                                Button.primary(event.getUser().getId() + ":disableXPSystem", "Disable").withDisabled(isDisabledBoolean)
                        ).queue();
                return;
            case "reject":
                event.getUser().openPrivateChannel().complete().sendMessage("Sorry, you are too young to use this bot! (You shouldn't be on Discord!)").queue();
                event.getMessage().delete().queue();
                return;
            case "noice":
            case "oh":
            case "old":
                embedBuilder = new EmbedBuilder().setTitle("Rules").setColor(InfoUserCommand.randomColor());
                String arrow = "<a:arrow_1:862525611465113640>";
                String message = arrow + " Be respectful to everyone. Do not be disruptive, rude, vulgar or otherwise act inappropriately towards other members in the call.\n" +
                        "\n" +
                        arrow + " Hate speech, including speech that is racist, sexist, or derogatory speech based on sexual orientation, are not allowed. No personal attacks are allowed.\n\n" +
                        arrow + " Having multiple accounts on the call/server is discouraged for all members.\n\n" +
                        arrow + " This is a family friendly server. All content, including images, names, and text shall be appropriate for all ages.\n\n" +
                        arrow + " No trolling and/or spamming.\n\n" +
                        arrow + " Discussions on conduct that violates the Terms of Service of Discord and Bots are not allowed. This includes but not limited to cheating, hacking, botting, and currency selling, as well as all other prohibited conduct.\n\n" +
                        arrow + " Members are not allowed in spamming referral codes or discord invitations, self-promotion, or advertising.\n\n" +
                        arrow + " No trading or begging. Links, when used for discussion, are allowed.\n\n" +
                        arrow + " Personal information includes but not limited to, pictures of members, may not be posted at any time for any reason.\n\n" +
                        arrow + " Information about this bot is unofficial unless provided directly by an employee of the bot. Server Moderators and Admins are not employees of the bot.\n\n" +
                        arrow + " [Discord's Partnership Code of Conduct](https://support.discordapp.com/hc/en-us/articles/360024871991-Discord-Partnership-Code-of-Conduct) and [Terms of Service](https://discordapp.com/terms) must be adhered to.";

                embedBuilder.setDescription(message);
                embedBuilder.setFooter("Press the Accept button if you accept the rules stated above!");

                String id = event.getUser().openPrivateChannel().complete().sendMessageEmbeds(embedBuilder.build()).setActionRow(
                        Button.primary("0000:accept", "Accept").withEmoji(Emoji.fromEmote("verify", Long.parseLong("803768813110951947"), true))
                ).complete().getPrivateChannel().getId();
                event.getMessage().delete().queue();
                return;
            case "n/a":
                return;
            default:
                event.deferReply().queue();
        }

        event.deferEdit().queue();
    }
}
