package com.general_hello.commands.commands.Logs;

import com.general_hello.commands.Bot;
import com.general_hello.commands.OtherEvents.OtherEvents;
import com.general_hello.commands.commands.Emoji.Emoji;
import com.general_hello.commands.commands.Utils.FormatUtil;
import com.general_hello.commands.commands.Utils.LogUtil;
import com.general_hello.commands.commands.Utils.Usage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class BasicLogger
{
    private final static String EDIT = "\u26A0"; // ⚠
    private final static String DELETE = "\u274C"; // ❌
    private final static String BULK_DELETE = "\uD83D\uDEAE"; // 🚮
    private final static String VIEW = "\uD83D\uDCC4"; // 📄
    private final static String DOWNLOAD = "\uD83D\uDCE9"; // 📩
    private final static String REDIRECT = "\uD83D\uDD00"; // 🔀
    private final static String REDIR_MID = "\uD83D\uDD39"; // 🔹
    private final static String REDIR_END = "\uD83D\uDD37"; // 🔷
    private final static String NAME = "\uD83D\uDCDB"; // 📛
    private final static String JOIN = "\uD83D\uDCE5"; // 📥
    private final static String NEW = "\uD83C\uDD95"; // 🆕
    private final static String LEAVE = "\uD83D\uDCE4"; // 📤
    private final static String AVATAR = "\uD83D\uDDBC"; // 🖼

    private final Usage usage = new Usage();

    public Usage getUsage()
    {
        return usage;
    }

    private void log(OffsetDateTime now, TextChannel tc, String emote, String message, MessageEmbed embed)
    {
        if (embed == null) {
            try
            {
                usage.increment(tc.getGuild().getIdLong());
                tc.sendMessage(new MessageBuilder()
                        .append(FormatUtil.filterEveryone(LogUtil.basiclogFormat(now, ZoneId.from(OffsetDateTime.now()), emote, message)))
                        .build()).queue();
            }
            catch(PermissionException ignore) {}
            return;
        }

        try
        {
            usage.increment(tc.getGuild().getIdLong());
            tc.sendMessage(new MessageBuilder()
                    .append(FormatUtil.filterEveryone(LogUtil.basiclogFormat(now, ZoneId.from(OffsetDateTime.now()), emote, message))).setEmbeds(embed)
                    .build()).queue();
        }
        catch(PermissionException ignore) {}
    }


    private void logFile(OffsetDateTime now, TextChannel tc, String emote, String message, byte[] file, String filename)
    {
        try
        {
            usage.increment(tc.getGuild().getIdLong());
            tc.sendMessage(FormatUtil.filterEveryone(LogUtil.basiclogFormat(now, ZoneId.from(LocalDateTime.now()), emote, message)))
                    .addFile(file, filename).queue();
        }
        catch(PermissionException ignore) {}
    }

    // Message Logs

    public void logMessageEdit(Message newMessage, MessageCache.CachedMessage oldMessage)
    {
        if(oldMessage==null)
            return;
        TextChannel mtc = oldMessage.getTextChannel();
        PermissionOverride po = mtc.getPermissionOverride(mtc.getGuild().getSelfMember());
        if(po!=null && po.getDenied().contains(Permission.MESSAGE_HISTORY))
            return;

        //TODO: ADD SETTINGS
        TextChannel tc = Bot.jda.getTextChannelById(844713439271321610L);
        if(tc==null)
            return;
        if(newMessage.getContentRaw().equals(oldMessage.getContentRaw()))
            return;
        EmbedBuilder edit = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .appendDescription("**From:** ")
                .appendDescription(FormatUtil.formatMessage(oldMessage));
        String newm = FormatUtil.formatMessage(newMessage);
        if(edit.getDescriptionBuilder().length()+9+newm.length()>2048)
            edit.addField("To:", newm.length()>1024 ? newm.substring(0,1016)+" (...)" : newm, false);
        else
            edit.appendDescription("\n**To:** "+newm);
        log(newMessage.getTimeEdited()==null ? newMessage.getTimeCreated(): newMessage.getTimeEdited(), tc, EDIT,
                FormatUtil.formatFullUser(newMessage.getAuthor())+" edited a message in "+newMessage.getTextChannel().getAsMention()+":", edit.build());
    }

    public void logMessageDelete(MessageCache.CachedMessage oldMessage)
    {
        if(oldMessage==null)
            return;
        Guild guild = oldMessage.getGuild();
        if(guild==null)
            return;
        TextChannel mtc = oldMessage.getTextChannel();
        PermissionOverride po = mtc.getPermissionOverride(guild.getSelfMember());
        if(po!=null && po.getDenied().contains(Permission.MESSAGE_HISTORY))
            return;
        TextChannel tc = Bot.jda.getTextChannelById(844713439271321610L);
        if(tc==null)
            return;
        String formatted = FormatUtil.formatMessage(oldMessage);
        if(formatted.isEmpty())
            return;

        User author = oldMessage.getAuthor();
        String user = author==null ? FormatUtil.formatCachedMessageFullUser(oldMessage) : FormatUtil.formatFullUser(author);

        EmbedBuilder delete = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("Message Deleted!")
                .appendDescription(DELETE + " " + user+"'s message has been deleted from "+mtc.getAsMention()+":\n" + formatted);

        tc.sendMessageEmbeds(delete.build()).queue();

    }

    public void logMessageBulkDelete(List<MessageCache.CachedMessage> messages, int count, TextChannel text)
    {
        if(count==0)
            return;
        TextChannel tc = Bot.jda.getTextChannelById(844713439271321610L);
        if(tc==null)
            return;
        if(messages.isEmpty())
        {
            log(OffsetDateTime.now(), tc, "\uD83D\uDEAE", "**"+count+"** messages were deleted from "+text.getAsMention()+" (**"+messages.size()+"** logged)", null);
            return;
        }
        TextChannel mtc = messages.get(0).getTextChannel();
        PermissionOverride po = mtc.getPermissionOverride(mtc.getGuild().getSelfMember());
        if(po!=null && po.getDenied().contains(Permission.MESSAGE_HISTORY))
            return;
        if(messages.size()==1)
        {
            String formatted = FormatUtil.formatMessage(messages.get(0));
            if(formatted.isEmpty())
                return;
            EmbedBuilder delete = new EmbedBuilder()
                    .setColor(Color.RED)
                    .appendDescription(formatted);
            User author = messages.get(0).getAuthor();
            String user = author==null ? FormatUtil.formatCachedMessageFullUser(messages.get(0)) : FormatUtil.formatFullUser(author);
            log(OffsetDateTime.now(), tc, DELETE, user+"'s message has been deleted from "+mtc.getAsMention()+":", delete.build());
            return;
        }
        OtherEvents.uploader.upload(LogUtil.logCachedMessagesForwards("Deleted Messages", messages), "DeletedMessages", (view, download) ->
        {
            log(OffsetDateTime.now(), tc, BULK_DELETE, "**"+count+"** messages were deleted from "+text.getAsMention()+" (**"+messages.size()+"** logged):",
                    new EmbedBuilder().setColor(Color.RED.darker().darker())
                            .appendDescription("[`"+VIEW+" View`]("+view+")  |  [`"+DOWNLOAD+" Download`]("+download+")").build());
        });
    }

    // Server Logs
    // Name change logs need to be handled specially because they are not guild-specific events, but only one
    // bot (pro vs normal) should ever log them.
    public void logNameChange(UserUpdateNameEvent event)
    {
        OffsetDateTime now = OffsetDateTime.now();
        getMutualGuilds(event.getUser().getIdLong()).stream()
                .map(guild -> Bot.jda.getTextChannelById(844713439271321610L))
                .filter(tc -> tc!=null)
                .forEachOrdered(tc ->
                {
                    log(now, tc, NAME, "**"+event.getOldName()+"**#"+event.getUser().getDiscriminator()+" (ID:"
                            +event.getUser().getId()+") has changed names to "+FormatUtil.formatUser(event.getUser()), null);
                });
    }


    private static Collection<Guild> getMutualGuilds(long userId)
    {
        User user = Bot.jda.getUserById(userId);
        if(user == null)
            return Collections.emptySet();
        return Bot.jda.getMutualGuilds(user);
    }

    public void logNameChange(UserUpdateDiscriminatorEvent event)
    {
        OffsetDateTime now = OffsetDateTime.now();
        getMutualGuilds(event.getUser().getIdLong()).stream()
                .map(guild -> Bot.jda.getTextChannelById(844713439271321610L))
                .filter(tc -> tc!=null)
                .forEachOrdered(tc ->
                {
                    log(now, tc, NAME, "**"+event.getUser().getName()+"**#"+event.getOldDiscriminator()+" (ID:"
                            +event.getUser().getId()+") has changed names to "+FormatUtil.formatUser(event.getUser()), null);
                });
    }

    public void logGuildJoin(GuildMemberJoinEvent event)
    {
        TextChannel tc = Bot.jda.getTextChannelById(844713439271321610L);
        if(tc==null)
            return;
        OffsetDateTime now = OffsetDateTime.now();
        long seconds = event.getUser().getTimeCreated().until(now, ChronoUnit.SECONDS);
        log(now, tc, JOIN, FormatUtil.formatFullUser(event.getUser())+" joined the server. "
                +(seconds<16*60 ? NEW : "")
                +"\nCreation: "+event.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)+" ("+FormatUtil.secondsToTimeCompact(seconds)+" ago)", null);
    }

    public void logGuildLeave(GuildMemberRemoveEvent event)
    {
        TextChannel tc = Bot.jda.getTextChannelById(844713439271321610L);
        if(tc==null)
            return;
        OffsetDateTime now = OffsetDateTime.now();
        String msg = FormatUtil.formatFullUser(event.getUser())+" left or was kicked from the server.";
        Member member = event.getMember();
        if(member != null)
        {
            long seconds = member.getTimeJoined().until(now, ChronoUnit.SECONDS);
            StringBuilder rlist;
            if(member.getRoles().isEmpty())
                rlist = new StringBuilder();
            else
            {
                rlist= new StringBuilder("\nRoles: `"+member.getRoles().get(0).getName());
                for(int i=1; i<member.getRoles().size(); i++)
                    rlist.append("`, `").append(member.getRoles().get(i).getName());
                rlist.append("`");
            }
            msg += "\nJoined: " + member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME)
                    + " (" + FormatUtil.secondsToTimeCompact(seconds) + " ago)" + rlist.toString();
        }
        log(now, tc, LEAVE, msg, null);
    }


    // Voice Logs

    public void logVoiceJoin(GuildVoiceJoinEvent event)
    {
        //TODO: VOice LOG CHANNEL
        TextChannel tc = Bot.jda.getTextChannelById(844713439271321610L);
        if(tc==null)
            return;
        log(OffsetDateTime.now(), tc, Emoji.BOOK, FormatUtil.formatFullUser(event.getMember().getUser())
                +" has joined voice channel _"+event.getChannelJoined().getName()+"_", null);
    }

    public void logVoiceMove(GuildVoiceMoveEvent event)
    {
        TextChannel tc = Bot.jda.getTextChannelById(844713439271321610L);
        if(tc==null)
            return;
        log(OffsetDateTime.now(), tc, Emoji.ARROW_POINTING_RIGHT, FormatUtil.formatFullUser(event.getMember().getUser())
                +" has moved voice channels from _"+event.getChannelLeft().getName()+"_ to _"+event.getChannelJoined().getName()+"_", null);
    }

    public void logVoiceLeave(GuildVoiceLeaveEvent event)
    {
        TextChannel tc = Bot.jda.getTextChannelById(844713439271321610L);
        if(tc==null)
            return;
        log(OffsetDateTime.now(), tc, Emoji.BABY_YODA, FormatUtil.formatFullUser(event.getMember().getUser())
                +" has left voice channel _"+event.getChannelLeft().getName()+"_", null);
    }
}