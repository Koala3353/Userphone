package com.general_hello.commands.commands.Logs;

import com.general_hello.commands.Bot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class MessageCache
{
    private final static int SIZE = 1000;
    private final HashMap<Long,FixedCache<Long,CachedMessage>> cache = new HashMap<>();

    public CachedMessage putMessage(Message m)
    {
        if(!cache.containsKey(m.getGuild().getIdLong()))
            cache.put(m.getGuild().getIdLong(), new FixedCache<>(SIZE));
        return cache.get(m.getGuild().getIdLong()).put(m.getIdLong(), new CachedMessage(m));
    }

    public CachedMessage pullMessage(Guild guild, long messageId)
    {
        if(!cache.containsKey(guild.getIdLong()))
            return null;
        return cache.get(guild.getIdLong()).pull(messageId);
    }

    public List<CachedMessage> getMessages(Guild guild, Predicate<CachedMessage> predicate)
    {
        if(!cache.containsKey(guild.getIdLong()))
            return Collections.EMPTY_LIST;
        return cache.get(guild.getIdLong()).getValues().stream().filter(predicate).collect(Collectors.toList());
    }

    public class CachedMessage implements ISnowflake
    {
        private final String content, username, discriminator;
        private final long id, author, channel, guild;
        private final List<Attachment> attachments;

        private CachedMessage(Message message)
        {
            content = message.getContentRaw();
            id = message.getIdLong();
            author = message.getAuthor().getIdLong();
            username = message.getAuthor().getName();
            discriminator = message.getAuthor().getDiscriminator();
            channel = message.getChannel().getIdLong();
            guild = message.isFromGuild() ? message.getGuild().getIdLong() : 0L;
            attachments = message.getAttachments();
        }

        public String getContentRaw()
        {
            return content;
        }

        public List<Attachment> getAttachments()
        {
            return attachments;
        }

        public User getAuthor()
        {
            return Bot.jda.getUserById(author);
        }

        public User getAuthor(ShardManager shardManager)
        {
            return shardManager.getUserById(author);
        }

        public String getUsername()
        {
            return username;
        }

        public String getDiscriminator()
        {
            return discriminator;
        }

        public long getAuthorId()
        {
            return author;
        }

        public TextChannel getTextChannel()
        {
            if (guild == 0L)
                return null;
            Guild g = Bot.jda.getGuildById(guild);
            if (g == null)
                return null;
            return g.getTextChannelById(channel);
        }

        public TextChannel getTextChannel(ShardManager shardManager)
        {
            if (guild == 0L)
                return null;
            Guild g = shardManager.getGuildById(guild);
            if (g == null)
                return null;
            return g.getTextChannelById(channel);
        }

        public long getTextChannelId()
        {
            return channel;
        }

        public TextChannel getTextChannel(Guild guild)
        {
            return guild.getTextChannelById(channel);
        }

        public Guild getGuild()
        {
            if (guild == 0L)
                return null;
            return Bot.jda.getGuildById(guild);
        }

        public Guild getGuild(ShardManager shardManager)
        {
            if (guild == 0L)
                return null;
            return shardManager.getGuildById(guild);
        }

        @Override
        public long getIdLong()
        {
            return id;
        }
    }
}
