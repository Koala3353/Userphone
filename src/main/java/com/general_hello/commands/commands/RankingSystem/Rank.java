package com.general_hello.commands.commands.RankingSystem;

import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import net.dv8tion.jda.api.entities.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Rank implements ICommand
{
    @Override
    public void handle(CommandContext event) throws InterruptedException, IOException, SQLException {
        List<String> args = event.getArgs();

        if (args.size() == 0) {
            User user = event.getAuthor();
            long xp = RankingSystem.getTotalXP(event.getGuild().getIdLong(), user.getIdLong());
            if (xp < 100) {
                event.getMessage().reply("You are not yet ranked!").queue();
                return;
            }
            event.getChannel().sendFile(RankingSystem.generateLevelCard(user, event.getGuild()), "card.png").queue();
        } else {
            String id = args.get(0).replaceAll("[^0-9]", "");
            event.getJDA().retrieveUserById(id).queue(
                    (user) ->
                    {
                        long xp = 0;
                        try {
                            xp = RankingSystem.getTotalXP(event.getGuild().getIdLong(), user.getIdLong());
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        if (xp < 100) {
                            event.getMessage().reply("This member is not yet ranked!").queue();
                            return;
                        }
                        event.getChannel().sendFile(RankingSystem.generateLevelCard(user, event.getGuild()), "card.png").queue();
                    },
                    (error) ->
                    {
                        User user = event.getAuthor();
                        long xp = 0;
                        try {
                            xp = RankingSystem.getTotalXP(event.getGuild().getIdLong(), user.getIdLong());
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        if (xp < 100) {
                            event.getMessage().reply("You are not yet ranked!").queue();
                            return;
                        }
                        event.getChannel().sendFile(RankingSystem.generateLevelCard(user, event.getGuild()), "card.png").queue();
                    }
            );

        }
    }

    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public String getHelp(String prefix) {
        return "Gets your current xp on this guild\n" +
                "Usage: `" + prefix + getName() + " (optional member)`";
    }
}
