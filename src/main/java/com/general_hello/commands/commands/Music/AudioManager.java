package com.general_hello.commands.commands.Music;

import com.general_hello.commands.Bot;
import com.general_hello.commands.OtherEvents.OnReadyEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
        OnReadyEvent.scheduledExecutorService.scheduleAtFixedRate(this::updateAll, 0, 5, TimeUnit.SECONDS);
    }

    private void updateAll()
    {
        for (Map.Entry<Long, CachedMessage> entry : nowPlayingMessages.entrySet())
        {
            long guildId = entry.getKey();
            CachedMessage cachedMessage = entry.getValue();
            Guild guild = Bot.jda.getGuildById(guildId);
            if (guild == null)
            {
                nowPlayingMessages.remove(guildId);
                continue;
            }

        }
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
