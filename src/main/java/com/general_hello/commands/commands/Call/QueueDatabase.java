package com.general_hello.commands.commands.Call;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;

public class QueueDatabase {
    public static ArrayList<TextChannel> queue = new ArrayList<>();
    public static ArrayList<Guild> guilds = new ArrayList<>();
    public static HashMap<Integer, ArrayList<TextChannel>> activeCall = new HashMap<>();
    public static int callId = 0;
    public static HashMap<TextChannel, Integer> retrieveCallId = new HashMap<>();
}