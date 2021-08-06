package com.general_hello.commands.OtherEvents;

import com.general_hello.commands.Database.DatabaseManager;
import com.general_hello.commands.commands.Info.InfoUserCommand;
import com.general_hello.commands.commands.Register.Data;
import com.general_hello.commands.commands.User.UserPhoneUser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OnPrivateMessage extends ListenerAdapter {
    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(InfoUserCommand.randomColor());
        if (Data.progress.containsKey(event.getAuthor())) {
            Integer progress = Data.progress.get(event.getAuthor());
            if (progress == 1) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(event.getMessage().getContentRaw());
                Data.answers.put(event.getAuthor(), arrayList);
                Data.progress.put(event.getAuthor(), 2);
                event.getChannel().sendMessage("Hello **" + event.getMessage().getContentRaw() + "**! What do you want your profile picture to be? (LINK ONLY)").queue();
                return;
            }

            ArrayList<String> oldAnswers = Data.answers.get(event.getAuthor());

            if (progress == 2) {
                embedBuilder.setTitle("Profile Picture");
                try {
                    embedBuilder.setImage(event.getMessage().getContentRaw());
                } catch (Exception e) {
                    event.getChannel().sendMessage("The link provided is invalid! Kindly try a different one!").queue();
                    return;
                }
                oldAnswers.add(event.getMessage().getContentRaw());
                Data.answers.put(event.getAuthor(), oldAnswers);
                Data.progress.put(event.getAuthor(), 100);

                embedBuilder.setDescription("[This](" + event.getMessage().getContentRaw() + ") image will be the profile picture that will be used with **Userphone**");
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                embedBuilder.clear();
                embedBuilder.setTitle("Account Summary");
                embedBuilder.setThumbnail(event.getMessage().getContentRaw());
                embedBuilder.setDescription("Name: ***" + oldAnswers.get(0) + "***\n" +
                        "Profile Picture: [Here](" + oldAnswers.get(1) + ")\n" +
                        "Agreed to all rules: ✅").setFooter("Thank you for registering!");
                embedBuilder.setColor(InfoUserCommand.randomColor());
                event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

                UserPhoneUser user = new UserPhoneUser(oldAnswers.get(0), event.getAuthor(), oldAnswers.get(1));
                Data.userUserPhoneUserHashMap.put(event.getAuthor(), user);
                Data.userPhoneUsers.add(user);

                System.out.println(oldAnswers.get(0) + "-" + oldAnswers.get(1));

                DatabaseManager.INSTANCE.newInfo(event.getAuthor().getIdLong(), oldAnswers.get(0), oldAnswers.get(1));
            }
        }
    }
}
