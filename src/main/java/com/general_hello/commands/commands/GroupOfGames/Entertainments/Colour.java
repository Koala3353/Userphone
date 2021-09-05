package com.general_hello.commands.commands.GroupOfGames.Entertainments;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Random;

public class Colour {
	static void colour(GuildMessageReceivedEvent event, String []args){
		if(args.length == 1) {
			Random rand = new Random();
			int r = rand.nextInt(256), g = rand.nextInt(256), b = rand.nextInt(256);
			Color color = new Color(r, g, b);
			String hex = String.format("%02X%02X%02X", r, g, b);
			EmbedBuilder Color = new EmbedBuilder()
				.setTitle("Color")
				.setDescription("Hex: #" + hex + "\nRGB: " + r + ", " + g + ", " + b)
				.setColor(color);
			event.getChannel().sendTyping().queue();
			event.getChannel().sendMessage(Color.build()).queue();
		}else if(args.length < 4) {
			String rgb = Color.decode("#" + Integer.parseInt(args[1], 16)).toString();
			rgb = rgb.substring(15, rgb.length()-1)
				.replaceAll("[rgb=]", "")
				.replaceAll(",", ", ");
			EmbedBuilder Color = new EmbedBuilder()
				.setTitle("Color")
				.setDescription("Hex: #" + args[1] + "\nRGB: " + rgb)
				.setColor(Integer.parseInt(args[1], 16));
			event.getChannel().sendTyping().queue();
			event.getChannel().sendMessageEmbeds(Color.build()).queue();
		}else {
			args[1] = args[1].replaceAll(",", "");
			args[2] = args[2].replaceAll(",", "");
			args[3] = args[3].replaceAll(",", "");
			int r = Integer.parseInt(args[1]);
			int g = Integer.parseInt(args[2]);
			int b = Integer.parseInt(args[3]);
			String hex = String.format("%02X%02X%02X", r, g, b);
			EmbedBuilder Color = new EmbedBuilder()
				.setTitle("Color")
				.setDescription("Hex: #" + hex + "\nRGB: " + r + ", " + g + ", " + b)
				.setColor(Integer.parseInt(hex, 16));
			event.getChannel().sendTyping().queue();
			event.getChannel().sendMessageEmbeds(Color.build()).queue();
		}
	}
}
