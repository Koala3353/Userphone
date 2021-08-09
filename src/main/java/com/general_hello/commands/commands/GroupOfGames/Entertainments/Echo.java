package com.general_hello.commands.commands.GroupOfGames.Entertainments;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Echo {
	static void echo(GuildMessageReceivedEvent event, String []args){
		if(event.getChannel().getSlowmode() >= 5) {
			event.getChannel().sendTyping().queue();
			event.getChannel().sendMessage("This command cannot be using in a channel with slowmode on").queue();
		}else {
			if(args.length == 1) {
				event.getChannel().sendMessage("Please add what you want me to echo.").queue();
			}else {
				StringBuilder echo = new StringBuilder();
				for(int i = 1; i < args.length; i++) {
					echo.append(" ").append(args[i]);
				}
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessage(echo.toString()).queue();
			}
		}
		
	}
}
