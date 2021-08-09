package com.general_hello.commands.commands.GroupOfGames.Entertainments;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Clap {

	static void clap(GuildMessageReceivedEvent event, String []args){
		StringBuilder message = new StringBuilder("👏");
		for(int i = 1; i < args.length; i++) {
			message.append(args[i]).append("👏");
		}
		event.getChannel().sendTyping().queue();
		event.getChannel().sendMessage(message.toString()).queue();
	}
}
