import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EntertainmentListener extends ListenerAdapter{
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, DatabaseManager.INSTANCE::getPrefix);
		String args = event.getMessage().getContentRaw();
		if(args.toLowerCase().startsWith(prefix + "clap ")) {
			Clap.clap(event);
			return;
		}
		if(args.toLowerCase().startsWith(prefix + "colour ")) {
			Colour.colour(event);
			return;
		}
		if(args.toLowerCase().startsWith(prefix + "echo ")) {
			Echo.echo(event);
			return;
		}
		if(args.toLowerCase().startsWith(prefix + "eightball ")) {
			Eightball.eightball(event);
			return;
		}
		if(args.toLowerCase().startsWith(prefix + "poll ")) {
			Poll.poll(event);
			return;
		}
		if(args.toLowerCase().startsWith(prefix + "read ")) {
			Read.read(event);
			return;
		}
		if(args.toLowerCase().startsWith(prefix + "rps ")) {
			RPS.rps(event);
			return;
		}
	}
}
