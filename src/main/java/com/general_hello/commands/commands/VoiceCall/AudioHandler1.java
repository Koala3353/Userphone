package com.general_hello.commands.commands.VoiceCall;

import com.general_hello.commands.Bot;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.TextChannel;

import java.nio.ByteBuffer;

public class AudioHandler1 implements AudioSendHandler, AudioReceiveHandler {

    private int port = onCallCommand.port;
    private boolean disconnected = false;

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        byte[] data = combinedAudio.getAudioData(1.0f);
        AudioStorage.audio.get(port).client1.add(data);
    }


    @Override
    public boolean canProvide() {
        return !AudioStorage.audio.get(port).client1.isEmpty();
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        while(disconnected) {System.out.print("");}
        try {
            AudioStorage.Audio audio = AudioStorage.audio.get(port);
            if(!audio.connected && audio.client1ID.equals("empty") && audio.client2ID.equals("")) {
                sendMessage(Bot.jda.getTextChannelById(audio.message1ID), "Uhoh, the call was disconnected.");
                Bot.jda.getGuildById(audio.client1ID).getAudioManager().setReceivingHandler(null);
                Bot.jda.getGuildById(audio.client1ID).getAudioManager().setSendingHandler(null);
                disconnected = true;
            }

            if(!(Bot.jda.getTextChannelById(audio.message1ID).getGuild().getAudioManager().isConnected())) {
                audio.connected = false;
                audio.client1ID = "empty";
                audio.client2ID = "";
                System.out.println("1");
                sendMessage(Bot.jda.getTextChannelById(audio.message1ID), "Uhoh, the call was disconnected.");
                Bot.jda.getGuildById(audio.client1ID).getAudioManager().setReceivingHandler(null);
                Bot.jda.getGuildById(audio.client1ID).getAudioManager().setSendingHandler(null);
                disconnected = true;
            }
        }catch(Exception e) {}
        byte[] data = AudioStorage.audio.get(port).client2.poll();
        return data == null ? null : ByteBuffer.wrap(data);
    }

    public static void sendMessage(TextChannel msgchannel, String content) {
        msgchannel.sendMessage(content).queue();
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}