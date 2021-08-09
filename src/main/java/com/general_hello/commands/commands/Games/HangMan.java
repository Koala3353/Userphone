package com.general_hello.commands.commands.Games;


import com.general_hello.commands.Listener;
import com.general_hello.commands.commands.Utils.MoneyData;
import com.general_hello.commands.commands.Utils.UtilNum;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HangMan implements Game{

    private static GuildMessageReceivedEvent e;
    private static final EmbedBuilder embedstart = new EmbedBuilder();
    private static final EmbedBuilder embedend = new EmbedBuilder();
    private static final EmbedBuilder embedgame = new EmbedBuilder();
    public static ArrayList<User> starter;

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private static String letter;
    private static HashMap<User, ArrayList<String>> word = new HashMap<>();
    private static HashMap<User, ArrayList<String>> guess = new HashMap<>(); //All guesses
    private static HashMap<User, ArrayList<String>> miss = new HashMap<>(); //Wrong guesses
    private static HashMap<User, ArrayList<String>> rights = new HashMap<>(); //Right guesses

    private static final int limit = 7;

    public static String hangman = "```_____________   \n"
            + "|           |   \n"
            + "|          " + "😨" + "  \n"
            + "|           |   \n"
            + "|          /|\\  \n"
            + "|          / \\  \n"
            + "|               \n"
            + "___             ```\n";

    public HangMan(GuildMessageReceivedEvent event)
    {
        e = event;

        starter.add(e.getAuthor());

        startGame(event.getAuthor());
    }

    @Override
    public void startGame(User user)
    {

        try {
            BufferedReader reader = new BufferedReader(new FileReader("/home/container/hangman.txt"));

            String ranword;
            long random = UtilNum.randomNum(1, 58109);
            long count = 0;

            while((ranword = reader.readLine() ) != null)
            {
                count++;
                if(random == count) break;
            }
            reader.close();
            System.out.println(ranword);

            //Clear Last GAME's data
            clear(user);

            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> rightsss = new ArrayList<>();

            //Initialize
            for(int i = 0; i < ranword.length(); i ++)
            {
                words.add(ranword.substring(i,i+1));
                rightsss.add("_");
            }

            word.put(user, words);
            rights.put(user, rightsss);

        } catch (IOException ioe) {
            LOGGER.info(this.getClass().getName(), "BufferReader at startGame()");
        }

        embedstart.setColor(Color.green);
        embedstart.addField("🎮 Hang Man: Game Started!",
                "Starter: " + user
                        + "\nWord length: " + word.size()
                        + "\n" + hangman, true);
        MessageEmbed me = embedstart.build();
        e.getChannel().sendMessageEmbeds(me).queue();
        embedstart.clearFields();

        printRightLetter(user);
    }

    @Override
    public void endGame(User user) { //End the GAME
        embedend.setColor(Color.green);
        embedend.setTitle( "🎮 Hang Man: Game Ended!", null);
        embedend.setFooter(e.getAuthor().getName() + " ended the game.", null);
        MessageEmbed me = embedend.build();
        e.getChannel().sendMessageEmbeds(me).queue();
        embedend.clearFields();

        String aword = "";

        ArrayList<String> words = word.get(user);
        for(String w : words)
        {
            aword += w + " ";
        }
        e.getChannel().sendMessage("The word was : `" + aword +"`").queue();
        clear(user);
    }

    @Override
    public void sendInput(List<String> in, GuildMessageReceivedEvent event) { //Get input
        e = event;
        if(in.size() > 1 || in.get(0).length() != 1)
            e.getChannel().sendMessage("🛑 One letter at a time!").queue();

        else if(!Character.isLetter(in.get(0).charAt(0)))
            e.getChannel().sendMessage("🛑 Please enter a valid letter.").queue();

        else if(word.size() == 0)
            e.getChannel().sendMessage("🛑 Game haven't started yet!").queue();

        else
        {
            letter = in.get(0);
            ArrayList<String> guessed = guess.get(event.getAuthor());
            ArrayList<String> right = rights.get(event.getAuthor());
            ArrayList<String> missed = miss.get(event.getAuthor());

            if(!guessed.contains(letter))
                guessed.add(letter);
            else
            {
                e.getChannel().sendMessage("🛑 This letter has been guessed before.").queue();
                return;
            }

            int countmiss = 0;
            for(int i = 0; i < word.size(); i ++)
            {
                if(letter.equals(word.get(i)))
                {
                    right.set(i, letter);
                }
                else
                {
                    countmiss++;
                }
            }

            if(countmiss == word.size())
            {
                missed.add(letter);

                if(missed.size() >= limit)
                {
                    endGame(event.getAuthor());
                    return;
                }
            }

            boolean end = checkWin(event.getAuthor());
            if(!end) print(event.getAuthor());
        }
    }

    public boolean checkWin(User user) //Check for winner
    {
        ArrayList<String> right = rights.get(user);

        for(int i = 0; i < word.size(); i++)
        {
            if(!right.get(i).equals(word.get(user).get(i)))
                return false;
        }

        int rewardbonus = 0;
        embedend.setColor(Color.green);
        embedend.setTitle("\uD83C\uDFAE Hang Man: Game finished!", null);
        embedend.addField("\uD83E\uDE99 " + (80000 + rewardbonus) + " more if you're Pro was added to your account", null, false);
        embedend.setFooter(e.getAuthor().getName() + " is the winner!", null);
        MessageEmbed me = embedend.build();
        e.getChannel().sendMessage(me).queue();

        final Double aDouble = MoneyData.money.get(e.getAuthor());
        MoneyData.money.put(e.getAuthor(), aDouble + 80000);
        embedend.clearFields();

        StringBuilder aword = new StringBuilder();

        ArrayList<String> words = word.get(user);
        for(String w : words)
        {
            aword.append(w).append(" ");
        }
        e.getChannel().sendMessage("The word was : `" + aword +"`").queue();
        return true;
    }

    public void clear(User user) //Clear all arraylist.
    {
        guess.remove(user);
        miss.remove(user);
        word.remove(user);
        rights.remove(user);
    }

    public void print(User user) //Print out the result
    {
        String missedletter = "";

        ArrayList<String> missed = miss.get(user);
        for(String s : missed)
        {
            missedletter += s + ", ";
        }

        if("".equals(missedletter))
            missedletter = "Missed Letters: None";
        else
            missedletter = "Missed Letters: " + missedletter.substring(0,missedletter.length()-2).toUpperCase();

        embedgame.setColor(Color.green);
        embedgame.setTitle("\uD83C\uDFAE Current Man (Hanged!?)", null);
        embedgame.setDescription(missedletter + "\n" + printHangMan(user));
        embedgame.setFooter("Guessed by " + e.getAuthor().getName(), e.getAuthor().getAvatarUrl());

        MessageEmbed me = embedgame.build();
        e.getChannel().sendMessageEmbeds(me).queue();
        embedgame.clearFields();

        printRightLetter(user);

    }

    public void printRightLetter(User user)
    {
        String rightletter = "`";
        ArrayList<String> right = rights.get(user);
        for(String s : right)
        {
            rightletter += s + " ";
        }
        rightletter += "`";
        e.getChannel().sendMessage(rightletter).queue();
    }

    public String printHangMan(User user)
    {
        String hangman;
        switch(miss.get(user).size())
        {
            case 0:
                hangman =   "```_____________   \n"
                        + "|           |   \n"
                        + "|               \n"
                        + "|               \n"
                        + "|                \n"
                        + "|                \n"
                        + "|                \n"
                        + "___              ```\n";
            case 1:
                hangman =   "```_____________   \n"
                        + "|           |   \n"
                        + "|          " + "😀" + "  \n"
                        + "|               \n"
                        + "|                \n"
                        + "|                \n"
                        + "|                \n"
                        + "___              ```\n";
                break;
            case 2:
                hangman =   "```_____________   \n"
                        + "|           |   \n"
                        + "|           " + "😋" + "  \n"
                        + "|           |   \n"
                        + "|                \n"
                        + "|                \n"
                        + "|                \n"
                        + "___              ```\n";
                break;
            case 3:
                hangman =   "```_____________   \n"
                        + "|           |   \n"
                        + "|           " + "😲" + "  \n"
                        + "|           |   \n"
                        + "|           |    \n"
                        + "|                \n"
                        + "|                \n"
                        + "___              ```\n";
                break;
            case 4:
                hangman =   "```_____________   \n"
                        + "|           |   \n"
                        + "|           " + "😲" + "  \n"
                        + "|           |  \n"
                        + "|          /|    \n"
                        + "|                \n"
                        + "|                \n"
                        + "___              ```\n";
                break;
            case 5:
                hangman =   "```_____________   \n"
                        + "|           |   \n"
                        + "|           " + "😲" + "  \n"
                        + "|           |   \n"
                        + "|          /|\\   \n"
                        + "|                \n"
                        + "|                \n"
                        + "___              ```\n";
                break;
            case 6:
                hangman =   "```_____________   \n"
                        + "|           |   \n"
                        + "|           " + "😶" + "  \n"
                        + "|           |   \n"
                        + "|          /|\\   \n"
                        + "|          /     \n"
                        + "|                \n"
                        + "___              ```\n";
                break;
            case 7:
                hangman =   "```_____________   \n"
                        + "|           |   \n"
                        + "|           " + "😐" + "  \n"
                        + "|           |   \n"
                        + "|          /|\\   \n"
                        + "|          / \\   \n"
                        + "|                \n"
                        + "___              ```\n";
                break;
            default:
                hangman = " ";
        }
        return hangman;
    }

}