package com.general_hello.commands.commands;

import com.general_hello.commands.Database.DatabaseManager;
import com.general_hello.commands.commands.Register.Data;
import com.general_hello.commands.commands.User.UserPhoneUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GetData {
    //get data from the db

    public void checkIfContainsData(User user, GuildMessageReceivedEvent ctx) {
        if (!Data.userUserPhoneUserHashMap.containsKey(user)) {
            retrieveData(user.getIdLong(), ctx);
        }
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

    private void retrieveData(Long userId, GuildMessageReceivedEvent ctx) {
        String name = DatabaseManager.INSTANCE.getName(userId);
        String profilePicture = DatabaseManager.INSTANCE.getProfilePictureLink(userId);

        if (name != null) {
            User userById = ctx.getJDA().getUserById(userId);
            UserPhoneUser user = new UserPhoneUser(name, userById, profilePicture);
            Data.userUserPhoneUserHashMap.put(userById, user);
            Data.userPhoneUsers.add(user);
        }
    }

    private void retrieveData(Long userId, CommandContext ctx) {
        String name = DatabaseManager.INSTANCE.getName(userId);
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
        String profilePicture = DatabaseManager.INSTANCE.getProfilePictureLink(userId);

        if (name != null) {
            User userById = ctx.getJDA().getUserById(userId);
            UserPhoneUser user = new UserPhoneUser(name, userById, profilePicture);
            Data.userUserPhoneUserHashMap.put(userById, user);
            Data.userPhoneUsers.add(user);
        }
    }
}
