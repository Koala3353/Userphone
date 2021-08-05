import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EntertainmentListener extends ListenerAdapter{
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String []args = event.getMessage().getContentRaw().split("\\s+");
		if(args[0].equalsIgnoreCase("u!clap")) {
			return;
		}
		if(args[0].equalsIgnoreCase("u!colour")) {
			return;
		}
		if(args[0].equalsIgnoreCase("u!echo")) {
			return;
		}
		if(args[0].equalsIgnoreCase("u!eightball")) {
			return;
		}
		if(args[0].equalsIgnoreCase("u!poll")) {
			return;
		}
		if(args[0].equalsIgnoreCase("u!read")) {
			return;
		}
		if(args[0].equalsIgnoreCase("u!rps")) {
			return;
		}
	}
}
