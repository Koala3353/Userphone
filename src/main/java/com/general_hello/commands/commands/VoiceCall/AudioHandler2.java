package com.general_hello.commands.commands.VoiceCall;

import com.general_hello.commands.Bot;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.TextChannel;

import java.nio.ByteBuffer;

public class AudioHandler2 implements AudioSendHandler, AudioReceiveHandler {

    private int port = onCallCommand.port;
    private boolean disconnected = false;

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        byte[] data = combinedAudio.getAudioData(1.0f);
        AudioStorage.audio[port].client2.add(data);
    }


    @Override
    public boolean canProvide() {
        return !AudioStorage.audio[port].client2.isEmpty();
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        while(disconnected) {System.out.print("");}
        try {
            AudioStorage.Audio audio = AudioStorage.audio[port];
            if(audio.connected == false && audio.client1ID.equals("empty") && audio.client2ID.equals("")) {
                sendMessage(Bot.jda.getTextChannelById(audio.message2ID), "Uhoh, the call was disconnected.");
                Bot.jda.getGuildById(audio.client2ID).getAudioManager().setReceivingHandler(null);
                Bot.jda.getGuildById(audio.client2ID).getAudioManager().setSendingHandler(null);
                disconnected = true;
            }
            if(!(Bot.jda.getTextChannelById(audio.message2ID).getGuild().getAudioManager().isConnected())) {
                audio.connected = false;
                audio.client1ID = "empty";
                audio.client2ID = "";
                System.out.println("2");
                sendMessage(Bot.jda.getTextChannelById(audio.message2ID), "Uh oh, the call was disconnected.");
                // Needs to be fixed, will spam the message
                Bot.jda.getGuildById(audio.client2ID).getAudioManager().setReceivingHandler(null);
                Bot.jda.getGuildById(audio.client2ID).getAudioManager().setSendingHandler(null);
                disconnected = true;
            }
        }catch(Exception e) {}
        byte[] data = AudioStorage.audio[port].client1.poll();
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
