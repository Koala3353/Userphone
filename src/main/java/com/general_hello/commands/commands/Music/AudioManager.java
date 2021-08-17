package com.general_hello.commands.commands.Music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AudioManager
{
    private static AudioPlayerManager playerManager;
    private static Map<Long, GuildAudioPlayer> audioPlayers;
    private static final Map<Long, CachedMessage> nowPlayingMessages = new HashMap<>();

    public AudioManager()
    {
        playerManager = new DefaultAudioPlayerManager();
        audioPlayers = new ConcurrentHashMap<>();
        AudioSourceManagers.registerRemoteSources(playerManager);
    }


    public static synchronized GuildAudioPlayer getAudioPlayer(long guildId)
    {
        if (audioPlayers.containsKey(guildId))
            return audioPlayers.get(guildId);
        GuildAudioPlayer player = new GuildAudioPlayer(playerManager, guildId);
        audioPlayers.put(guildId, player);
        return player;
    }

    public static AudioPlayerManager getPlayerManager()
    {
        return playerManager;
    }

    public CachedMessage getLastNPMessage(long guildId)
    {
        return nowPlayingMessages.get(guildId);
    }

    public void setLastNPMessage(long guildId, Message message)
    {
        nowPlayingMessages.put(guildId, new CachedMessage(message));
    }
}
