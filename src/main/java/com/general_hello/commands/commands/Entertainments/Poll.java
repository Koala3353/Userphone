package com.general_hello.commands.commands.Entertainments;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Poll {
	static void poll(GuildMessageReceivedEvent event){
		String [] args = event.getMessage().getContentRaw().split("\\s+");
		event.getMessage().addReaction("✅").queue();
		event.getMessage().addReaction("❌").queue();
		event.getMessage().addReaction("➖").queue();
	}
	
	static void newpoll(GuildMessageReceivedEvent event, String []args){
		String question = "";
		for(int i = 1; i < args.length; i++) {
			question = question + " " + args[i];
		}
		event.getChannel().sendTyping().queue();
		event.getChannel().sendMessage("VOTE NOW").queue();
		EmbedBuilder poll = new EmbedBuilder()
				.setTitle("POLL")
				.setColor(0x7289DA)
				.setDescription(question);
		event.getChannel().sendTyping().queue();
		event.getChannel().sendMessage(poll.build()).queue(message -> {
			message.addReaction("✅").queue();
			message.addReaction("❌").queue();
			message.addReaction("➖").queue();
		});
		poll.clear();
	}
}
