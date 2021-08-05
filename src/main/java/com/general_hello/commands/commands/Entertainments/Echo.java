import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Echo extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		try {
			if (args[0].equalsIgnoreCase(prefix + "echo")) {
				if(event.getChannel().getSlowmode() >= 5) {
					event.getChannel().sendTyping().queue();
					event.getChannel().sendMessage("This command cannot be using in a channel with slowmode on").queue();
				}else {
					if(args.length == 1) {
						event.getChannel().sendMessage("Please add what you want me to echo.").queue();
					}else {
						String echo = "";
						for(int i = 1; i < args.length; i++) {
							echo = echo + " " + args[i];
						}
						event.getChannel().sendTyping().queue();
						event.getChannel().sendMessage(echo).queue();
					}
				}
			}
		}catch(Exception e) {
			System.out.println("Echo: " + event.getGuild());

		}

	}
}

