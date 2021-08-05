package com.general_hello.commands.commands.VoiceCall;

import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import net.dv8tion.jda.api.entities.Message;

import java.io.IOException;

public class onLeaveCommand implements ICommand {
    @Override
    public void handle(CommandContext event) throws InterruptedException, IOException {
        Message message = event.getMessage();
        if(!event.getGuild().getAudioManager().isConnected()) {
            replyMessage(message, "I'm not in a voice channel(?)");
            reactMessage(message, "🤔");
            return;
        }
        event.getGuild().getAudioManager().closeAudioConnection();
        replyMessage(message, "Left the voice channel");
        reactMessage(message, "👍");
    }

    private static void replyMessage(Message message, String content) {
        message.reply(content).queue();
    }

    private static void reactMessage(Message message, String emoji) {
        message.addReaction(emoji).queue();;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getHelp(String prefix) {
        return "Makes the bot leave the channel.\n" +
                "Usage: `" + prefix + getName() + "`";
    }
}
