package com.general_hello.commands.commands.Call;

import com.general_hello.commands.Database.DatabaseManager;
import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import com.general_hello.commands.commands.PrefixStoring;
import com.general_hello.commands.commands.Register.Data;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.io.IOException;
import java.util.ArrayList;

public class JoinCallQueueCommand implements ICommand {
    @Override
    public void handle(CommandContext ctx) throws InterruptedException, IOException {
        User user = ctx.getAuthor();
        TextChannel channel = ctx.getChannel();

        final long guildID = ctx.getGuild().getIdLong();
        String prefix = PrefixStoring.PREFIXES.computeIfAbsent(guildID, DatabaseManager.INSTANCE::getPrefix);

        if (!Data.userUserPhoneUserHashMap.containsKey(user)) {
            ctx.getChannel().sendMessage("Kindly register first before calling! Use ***" + prefix + "register*** to register").queue();
            return;
        }
        if (QueueDatabase.queue.contains(channel)) {
            ctx.getChannel().sendMessage("It seems like your calling to someone already!").queue();
            return;
        }
        if (QueueDatabase.guilds.contains(ctx.getGuild())) {
            ctx.getChannel().sendMessage("It seems like you or someone else is calling to someone already!").queue();
            return;
        }
        QueueDatabase.queue.add(channel);
        ctx.getChannel().sendMessage("<a:babyyoda:866105061665669140> Calling to random users.......").queue();

        if (QueueDatabase.queue.size() == 2) {
            ArrayList<TextChannel> oof = new ArrayList<>();
            oof.add(QueueDatabase.queue.get(0));
            QueueDatabase.queue.get(0).sendMessage("<a:book:866105059851239444> Call has been responded! Say `hello`!").queue();
            QueueDatabase.retrieveCallId.put(QueueDatabase.queue.get(0), QueueDatabase.callId);
            QueueDatabase.queue.remove(0);
            QueueDatabase.retrieveCallId.put(QueueDatabase.queue.get(0), QueueDatabase.callId);
            oof.add(QueueDatabase.queue.get(0));
            QueueDatabase.queue.remove(0);
            QueueDatabase.guilds.add(ctx.getGuild());

            QueueDatabase.activeCall.put(QueueDatabase.callId, oof);
            QueueDatabase.callId++;
            ctx.getChannel().sendMessage("<a:book:866105059851239444> Call has been responded! Say `hello`!").queue();
        }
    }

    @Override
    public String getName() {
        return "call";
    }

    @Override
    public String getHelp(String prefix) {
        return "Makes a call to random users";
    }
}
