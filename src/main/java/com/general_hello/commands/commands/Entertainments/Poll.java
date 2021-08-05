import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Poll extends ListenerAdapter{
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String [] args = event.getMessage().getContentRaw().split("\\s+");
		try {
			if (args[0].equalsIgnoreCase(prefix + "poll")){
				event.getMessage().addReaction("✅").queue();
				event.getMessage().addReaction("❌").queue();
				event.getMessage().addReaction("➖").queue();
			}
			if (args[0].equalsIgnoreCase("=pollnew")){
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
		}catch(Exception e) {
			System.out.println("Poll: " + event.getGuild());
		}
	}
}
