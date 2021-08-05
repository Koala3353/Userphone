package com.general_hello.commands.commands.VoiceCall;

import com.general_hello.commands.Bot;
import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;

public class onCallAnonCommand implements ICommand {
    static int port;

    @Override
    public void handle(CommandContext event) throws InterruptedException, IOException {
        Member member = event.getMember();
        GuildVoiceState voiceState = member.getVoiceState();
        VoiceChannel vcchannel = voiceState.getChannel();
        Message message = event.getMessage();
        String vcname = vcchannel.getName();
        try {
            if(event.getGuild().getAudioManager().isConnected()) {
                replyMessage(message, "Looks like I'm already in a voice channel... Can't join, sorry");
                reactMessage(message, "🤔");
            }else if(!event.getGuild().getSelfMember().hasPermission(vcchannel, Permission.VOICE_CONNECT)) {
                replyMessage(message, "I do not have permission to join `" + vcname + "`...");
                reactMessage(message, "👎");
            }else if (vcchannel != null) {
                event.getGuild().getAudioManager().openAudioConnection(vcchannel);
                replyMessage(message, "Connected to `" + vcname + "`");
                findOpenPort(vcchannel, message);
                reactMessage(message, "👍");
            }
        }catch(Exception e) {e.printStackTrace();}
    }

    private static void replyMessage(Message message, String content) {
        message.reply(content).queue();
    }

    private static void reactMessage(Message message, String emoji) {
        message.addReaction(emoji).queue();;
    }

    private static void findOpenPort(VoiceChannel vcchannel, Message message) {
        Guild guild = vcchannel.getGuild();
        String guildID = guild.getId();
        String channelID = message.getChannel().getId();
        for(int i = 0; i < AudioStorage.audio.size(); i++) {
            AudioStorage.Audio audio = AudioStorage.audio.get(i);
            if(audio.connected == false) {
                if(!audio.client1ID.equals("empty")) {
                    audio.client2ID = guildID;
                    audio.message2ID = channelID;
                    audio.connected = true;
                    connectTo(vcchannel, i, false);
                    sendMessage(Bot.jda.getTextChannelById(audio.message1ID), "Someone picked up the phone!");
                    sendMessage(Bot.jda.getTextChannelById(audio.message2ID), "Calling...");
                    sendMessage(Bot.jda.getTextChannelById(audio.message2ID), "Someone picked up the phone!");
                    System.out.println(audio.message1ID + "-" + audio.message2ID);
                    System.out.println(audio.client1ID + "-" + audio.client2ID);
                    return;
                }else if(audio.client1ID.equals("empty")) {
                    audio.client1ID = guildID;
                    audio.message1ID = channelID;
                    connectTo(vcchannel, i, true);
                    sendMessage(Bot.jda.getTextChannelById(audio.message1ID), "Calling...");
                    return;
                }
            }
        }
        replyMessage(message, "Hmmm, I was unable to connect to a port!");
        reactMessage(message, "😕");
    }

    private static void connectTo(VoiceChannel channel, int inport, boolean await) {
        Guild guild = channel.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        port = inport;
        if(await) {
            AudioHandler2 handler = new AudioHandler2();
            audioManager.setSendingHandler(handler);
            audioManager.setReceivingHandler(handler);
        }else {
            AudioHandler1 handler = new AudioHandler1();
            audioManager.setSendingHandler(handler);
            audioManager.setReceivingHandler(handler);
        }
    }

    static void sendMessage(TextChannel msgchannel, String content) {
        msgchannel.sendMessage(content).queue();
    }

    @Override
    public String getName() {
        return "call anon";
    }

    @Override
    public String getHelp(String prefix) {
        return "Calls other users anonymously\n" +
                "Usage: `" + prefix + getName() + "`";
    }
}
