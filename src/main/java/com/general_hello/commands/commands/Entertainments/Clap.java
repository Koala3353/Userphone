import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Clap extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String []args = event.getMessage().getContentRaw().split("\\s+");
		try {
			if (args[0].toLowerCase().contains((prefix + "clap")) && !event.getAuthor().isBot()) {
				String message = "👏";
				for(int i = 1; i < args.length; i++) {
					message = message + args[i] + "👏";
				}
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessage(message).queue();
			}
		}catch(Exception e) {
			System.out.println("Clap: " + event.getGuild());
		}
	}
}