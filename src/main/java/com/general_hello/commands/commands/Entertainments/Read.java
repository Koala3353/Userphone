import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Read extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		try {
			if (args[0].equalsIgnoreCase(prefix + "read")) {
				Message message = event.getMessage();
				if(message.getAttachments().size() == 0){
					event.getChannel().sendMessage("Attach a file that you want to compile!").queue();
				}else{
					InputStream stream;
					try {
						stream = message.getAttachments().get(0).retrieveInputStream().get();
						ArrayList<Character> arr = new ArrayList<>();
						int b;
						while((b = stream.read()) != -1)
							arr.add((char) b);
						char[] unboxed = new char[arr.size()];
						for(int i = 0; i < unboxed.length; i++)
							unboxed[i] = arr.get(i);
						String s = String.valueOf(unboxed);
						EmbedBuilder text = new EmbedBuilder()
								.setTitle("Recieved a file! \nBytes read:")
								.setColor(0x7289DA)
								.setDescription(s);
						event.getChannel().sendMessage(text.build()).queue();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						event.getChannel().sendMessage("*Error, Looks like your document has more than 2048 characters!*").queue();
					}
				}
			}
		}catch(Exception e) {
			System.out.println("Read: " + event.getGuild());
		}
	}
}
