package com.general_hello.commands.commands.Call;

import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;
import java.util.ArrayList;

public class HangupCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws InterruptedException, IOException {
        Integer callId = QueueDatabase.retrieveCallId.get(ctx.getChannel());
        ArrayList<TextChannel> callID = QueueDatabase.activeCall.get(callId);
        QueueDatabase.activeCall.remove(callId);
        QueueDatabase.retrieveCallId.remove(callID.get(0));
        QueueDatabase.retrieveCallId.remove(callID.get(1));
        QueueDatabase.guilds.remove(callID.get(0).getGuild());
        QueueDatabase.guilds.remove(callID.get(1).getGuild());

        callID.get(0).sendMessage("The call has been ended by a user!").queue();
        callID.get(1).sendMessage("The call has been ended by a user!").queue();
    }

    @Override
    public String getName() {
        return "hangup";
    }

    @Override
    public String getHelp(String prefix) {
        return "Ends the call\n" +
                "Usage: `" + prefix + getName() + "`";
    }
}
