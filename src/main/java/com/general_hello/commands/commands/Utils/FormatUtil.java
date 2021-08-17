package com.general_hello.commands.commands.Utils;


import com.general_hello.commands.commands.Logs.MessageCache;
import net.dv8tion.jda.api.entities.*;

import java.util.List;
import java.util.function.Function;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class FormatUtil {

    private final static String MULTIPLE_FOUND = "**Multiple %s found matching \"%s\":**";
    private final static String CMD_EMOJI = "\uD83D\uDCDC"; // 📜

    public static String filterEveryone(String input)
    {
        return input.replace("\u202E","") // RTL override
                .replace("@everyone","@\u0435veryone") // cyrillic e
                .replace("@here","@h\u0435re") // cyrillic e
                .replace("discord.gg/", "discord\u2024gg/") // one dot leader
                .replace("@&", "\u0DB8&"); // role failsafe
    }

    public static String formatMessage(Message m)
    {
        StringBuilder sb = new StringBuilder(m.getContentRaw());
        m.getAttachments().forEach(att -> sb.append("\n").append(att.getUrl()));
        return sb.length()>2048 ? sb.toString().substring(0, 2040) : sb.toString();
    }

    public static String formatMessage(MessageCache.CachedMessage m)
    {
        StringBuilder sb = new StringBuilder(m.getContentRaw());
        m.getAttachments().forEach(att -> sb.append("\n").append(att.getUrl()));
        return sb.length()>2048 ? sb.toString().substring(0, 2040) : sb.toString();
    }

    public static String formatFullUserId(long userId)
    {
        return "<@"+userId+"> (ID:"+userId+")";
    }

    public static String formatCachedMessageFullUser(MessageCache.CachedMessage msg)
    {
        return filterEveryone("**"+msg.getUsername()+"**#"+msg.getDiscriminator()+" (ID:"+msg.getAuthorId()+")");
    }

    public static String formatUser(User user)
    {
        return filterEveryone("**"+user.getName()+"**#"+user.getDiscriminator());
    }

    public static String formatFullUser(User user)
    {
        return filterEveryone("**"+user.getName()+"**#"+user.getDiscriminator()+" (ID:"+user.getId()+")");
    }

    public static String capitalize(String input)
    {
        if(input==null || input.isEmpty())
            return "";
        if(input.length()==1)
            return input.toUpperCase();
        return Character.toUpperCase(input.charAt(0))+input.substring(1).toLowerCase();
    }

    public static String join(String delimiter, char... items)
    {
        if(items==null || items.length==0)
            return "";
        StringBuilder sb = new StringBuilder().append(items[0]);
        for(int i=1; i<items.length; i++)
            sb.append(delimiter).append(items[i]);
        return sb.toString();
    }

    public static <T> String join(String delimiter, Function<T,String> function, T... items)
    {
        if(items==null || items.length==0)
            return "";
        StringBuilder sb = new StringBuilder(function.apply(items[0]));
        for(int i=1; i<items.length; i++)
            sb.append(delimiter).append(function.apply(items[i]));
        return sb.toString();
    }

    public static String listOfVoice(List<VoiceChannel> list, String query)
    {
        String out = String.format(MULTIPLE_FOUND, "voice channels", query);
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String listOfRoles(List<Role> list, String query)
    {
        String out = String.format(MULTIPLE_FOUND, "roles", query);
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String listOfText(List<TextChannel> list, String query)
    {
        String out = String.format(MULTIPLE_FOUND, "text channels", query);
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - "+list.get(i).getName()+" ("+list.get(i).getAsMention()+")";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String listOfUser(List<User> list, String query)
    {
        String out = String.format(MULTIPLE_FOUND, "users", query);
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - **"+list.get(i).getName()+"**#"+list.get(i).getDiscriminator()+" (ID:"+list.get(i).getId()+")";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String listOfMember(List<Member> list, String query)
    {
        String out = String.format(MULTIPLE_FOUND, "members", query);
        for(int i=0; i<6 && i<list.size(); i++)
            out+="\n - **"+list.get(i).getUser().getName()+"**#"+list.get(i).getUser().getDiscriminator()+" (ID:"+list.get(i).getUser().getId()+")";
        if(list.size()>6)
            out+="\n**And "+(list.size()-6)+" more...**";
        return out;
    }

    public static String secondsToTime(long timeseconds)
    {
        StringBuilder builder = new StringBuilder();
        int years = (int)(timeseconds / (60*60*24*365));
        if(years>0)
        {
            builder.append("**").append(years).append("** years, ");
            timeseconds = timeseconds % (60*60*24*365);
        }
        int weeks = (int)(timeseconds / (60*60*24*7));
        if(weeks>0)
        {
            builder.append("**").append(weeks).append("** weeks, ");
            timeseconds = timeseconds % (60*60*24*7);
        }
        int days = (int)(timeseconds / (60*60*24));
        if(days>0)
        {
            builder.append("**").append(days).append("** days, ");
            timeseconds = timeseconds % (60*60*24);
        }
        int hours = (int)(timeseconds / (60*60));
        if(hours>0)
        {
            builder.append("**").append(hours).append("** hours, ");
            timeseconds = timeseconds % (60*60);
        }
        int minutes = (int)(timeseconds / (60));
        if(minutes>0)
        {
            builder.append("**").append(minutes).append("** minutes, ");
            timeseconds = timeseconds % (60);
        }
        if(timeseconds>0)
            builder.append("**").append(timeseconds).append("** seconds");
        String str = builder.toString();
        if(str.endsWith(", "))
            str = str.substring(0,str.length()-2);
        if(str.isEmpty())
            str="**No time**";
        return str;
    }

    public static String secondsToTimeCompact(long timeseconds)
    {
        StringBuilder builder = new StringBuilder();
        int years = (int)(timeseconds / (60*60*24*365));
        if(years>0)
        {
            builder.append("**").append(years).append("**y ");
            timeseconds = timeseconds % (60*60*24*365);
        }
        int weeks = (int)(timeseconds / (60*60*24*7));
        if(weeks>0)
        {
            builder.append("**").append(weeks).append("**w ");
            timeseconds = timeseconds % (60*60*24*7);
        }
        int days = (int)(timeseconds / (60*60*24));
        if(days>0)
        {
            builder.append("**").append(days).append("**d ");
            timeseconds = timeseconds % (60*60*24);
        }
        int hours = (int)(timeseconds / (60*60));
        if(hours>0)
        {
            builder.append("**").append(hours).append("**h ");
            timeseconds = timeseconds % (60*60);
        }
        int minutes = (int)(timeseconds / (60));
        if(minutes>0)
        {
            builder.append("**").append(minutes).append("**m ");
            timeseconds = timeseconds % (60);
        }
        if(timeseconds>0)
            builder.append("**").append(timeseconds).append("**s");
        String str = builder.toString();
        if(str.endsWith(", "))
            str = str.substring(0,str.length()-2);
        if(str.isEmpty())
            str="**No time**";
        return str;
    }
}
