package com.general_hello.commands.commands.Entertainments;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Clap {
	static void clap(GuildMessageReceivedEvent event, String []args){
		//test
		String message = "👏";
		for(int i = 1; i < args.length; i++) {
			message = message + args[i] + "👏";
		}
		event.getChannel().sendTyping().queue();
		event.getChannel().sendMessage(message).queue();
	}
}
