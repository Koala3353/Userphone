package com.general_hello.commands.commands;

import com.general_hello.commands.Database.DatabaseManager;
import com.general_hello.commands.commands.Register.Data;
import com.general_hello.commands.commands.User.UserPhoneUser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.sql.SQLException;

public class GetData {
    //get data from the db

    public int checkIfContainsData(User user, GuildMessageReceivedEvent ctx) {
        if (!Data.userUserPhoneUserHashMap.containsKey(user)) {
            return retrieveData(user.getIdLong(), ctx);
        }

        return 100;
    }

    public void checkIfContainsData(User user, SlashCommandEvent ctx) {
        if (!Data.userUserPhoneUserHashMap.containsKey(user)) {
            retrieveData(user.getIdLong(), ctx);
        }
    }

    public void checkIfContainsData(User user, CommandContext ctx) {
        if (!Data.userUserPhoneUserHashMap.containsKey(user)) {
            retrieveData(user.getIdLong(), ctx);
        }
    }

    public static long getLevelPoints(Member member) throws SQLException {
        return DatabaseManager.INSTANCE.getXpPoints(member.getIdLong());
    }

    public static void setLevelPoints(Member user, long points) {
        DatabaseManager.INSTANCE.setXpPoints(user.getIdLong(), points);
    }

    private int retrieveData(Long userId, GuildMessageReceivedEvent ctx) {
        String name = DatabaseManager.INSTANCE.getName(userId);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String profilePicture = DatabaseManager.INSTANCE.getProfilePictureLink(userId);

        if (name != null) {
            User userById = ctx.getJDA().getUserById(userId);
            UserPhoneUser user = new UserPhoneUser(name, userById, profilePicture);
            Data.userUserPhoneUserHashMap.put(userById, user);
            Data.userPhoneUsers.add(user);
            return 0;
        }

        return -1;
    }

    private void retrieveData(Long userId, CommandContext ctx) {
        String name = DatabaseManager.INSTANCE.getName(userId);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String profilePicture = DatabaseManager.INSTANCE.getProfilePictureLink(userId);
        if (name != null) {
            User userById = ctx.getJDA().getUserById(userId);
            UserPhoneUser user = new UserPhoneUser(name, userById, profilePicture);
            Data.userUserPhoneUserHashMap.put(userById, user);
            Data.userPhoneUsers.add(user);
        }
    }

    private void retrieveData(Long userId, SlashCommandEvent ctx) {
        String name = DatabaseManager.INSTANCE.getName(userId);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String profilePicture = DatabaseManager.INSTANCE.getProfilePictureLink(userId);
        if (name != null) {
            User userById = ctx.getJDA().getUserById(userId);
            UserPhoneUser user = new UserPhoneUser(name, userById, profilePicture);
            Data.userUserPhoneUserHashMap.put(userById, user);
            Data.userPhoneUsers.add(user);
        }
    }
}
