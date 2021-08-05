package com.general_hello.commands.commands.RankingSystem;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadFactory;

public class Util
{

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    /**
     * Auto closes AutoClosables
     *
     * @param closeables Closeables
     */
    public static void closeQuietly(AutoCloseable... closeables)
    {
        for (AutoCloseable c : closeables)
        {
            if (c != null)
            {
                try
                {
                    c.close();
                } catch (Exception ignored)
                {
                }
            }
        }
    }

    public static String titleMarkdown(AudioTrack track)
    {
        return "[" + track.getInfo().title + "](<" + track.getInfo().uri + ">)";
    }

    public static String timeFormat(long seconds)
    {
        return (new SimpleDateFormat("HH:mm:ss")).format(new Date(seconds));
    }

    public static void sendPM(User user, Message message)
    {
        user.openPrivateChannel()
                .flatMap(x -> x.sendMessage(message))
                .queue(s ->
                {
                }, e ->
                {
                });
    }

    public static void sendPM(User user, CharSequence sequence)
    {
        user.openPrivateChannel()
                .flatMap(x -> x.sendMessage(sequence))
                .queue(s ->
                {
                }, e ->
                {
                });
    }

    public static void sendPM(User user, MessageEmbed embed)
    {
        user.openPrivateChannel()
                .flatMap(x -> x.sendMessage(embed))
                .queue(s ->
                {
                }, e ->
                {
                });
    }

    public static ThreadFactory newThreadFactory(String threadName, Logger logger)
    {
        return newThreadFactory(threadName, logger, true);
    }

    public static ThreadFactory newThreadFactory(String threadName, Logger logger, boolean isdaemon)
    {
        return (r) ->
        {
            Thread t = new Thread(r, threadName);
            t.setDaemon(isdaemon);
            t.setUncaughtExceptionHandler((final Thread thread, final Throwable throwable) ->
                    logger.error("There was a uncaught exception in the {} threadpool", thread.getName(), throwable));
            return t;
        };
    }

    public static String replaceLast(final String text, final String regex, final String replacement)
    {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}