package com.general_hello.commands.commands.GroupOfGames.Games;


import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 *
 * @author Alien Ideology <alien.ideology at alien.org>
 */
public interface Game {

    void startGame(User user);

    void endGame(User user); //Stop da game

    /*{
        if(e.getAuthor() == starter || e.getAuthor() == opponent)
            e.getChannel().sendMessage(Emoji.E_success + " game Ended!").queue();
    }*/

    void sendInput(List<String> in, GuildMessageReceivedEvent event);  //Set the input called by a command class


    /*{
    public void switchTurn();
        if("starter".equals(turn))
            turn = opponent;
        else
            turn = starter;
    }*/
}