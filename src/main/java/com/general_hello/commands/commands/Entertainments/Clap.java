import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Clap {
	static void clap(GuildMessageReceivedEvent event, String []args){
		String message = "👏";
		for(int i = 1; i < args.length; i++) {
			message = message + args[i] + "👏";
		}
		event.getChannel().sendTyping().queue();
		event.getChannel().sendMessage(message).queue();
	}
}
