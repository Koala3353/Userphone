package com.general_hello.commands.commands.MiniGames;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;

public class Connect4Storage {
    public static HashMap<TextChannel, ArrayList<User>> connect4Players = new HashMap<>();
}
