package com.general_hello.commands.commands.Logs;

import com.general_hello.commands.OtherEvents.OtherEvents;
import com.general_hello.commands.commands.Emoji.Emoji;
import com.general_hello.commands.commands.Utils.OtherUtil;
import com.general_hello.commands.commands.Utils.Usage;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class AutoMod
{
    private static final Pattern INVITES = Pattern.compile("discord\\s?(?:(?:\\.|dot|\\(\\.\\)|\\(dot\\))\\s?gg|(?:app)?\\s?\\.\\s?com\\s?\\/\\s?invite)\\s?\\/\\s?([A-Z0-9-]{2,18})",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern REF = Pattern.compile("https?:\\/\\/\\S+(?:\\/ref\\/|[?&#]ref(?:errer|erral)?=)\\S+", Pattern.CASE_INSENSITIVE);
    private static final Pattern BASE_URL = Pattern.compile("https?:\\/\\/(?:[^?&:\\/\\s]+\\.)?([^?&:\\/\\s]+\\.\\w+)(?:\\W|$)", Pattern.CASE_INSENSITIVE);

    private static final Pattern LINK       = Pattern.compile("https?:\\/\\/\\S+", Pattern.CASE_INSENSITIVE);
    private static final String INVITE_LINK = "https?:\\/\\/discord(?:app\\.com\\/invite|\\.com\\/invite|\\.gg)\\/(\\S+)";

    private static final Logger LOG = LoggerFactory.getLogger("AutoMod");

    private String[] refLinkList;
    private final InviteResolver inviteResolver = new InviteResolver();
    private final CopypastaResolver copypastaResolver = new CopypastaResolver();
    private final Usage usage = new Usage();

    public AutoMod()
    {
        loadCopypastas();
        loadReferralDomains();
    }

    public final void loadCopypastas()
    {
        this.copypastaResolver.load();
    }


    public final void loadReferralDomains()
    {
        this.refLinkList = OtherUtil.readLines("referral_domains");
    }

    public Usage getUsage()
    {
        return usage;
    }

    private boolean shouldPerformAutomod(Member member, TextChannel channel)
    {
        // ignore users not in the guild
        if(member==null)
            return true;

        // ignore broken guilds
        if(member.getGuild().getOwner()==null)
            return true;

        // ignore bots
        if(member.getUser().isBot())
            return true;

        // ignore users vortex cant interact with
        if(!member.getGuild().getSelfMember().canInteract(member))
            return true;

        // ignore users that can kick
        if(member.hasPermission(Permission.KICK_MEMBERS))
            return true;

        // ignore users that can ban
        if(member.hasPermission(Permission.BAN_MEMBERS))
            return true;

        // ignore users that can manage server
        if(member.hasPermission(Permission.MANAGE_SERVER))
            return true;

        // if a channel is specified, ignore users that can manage messages in that channel
        if(channel!=null && (member.hasPermission(channel, Permission.MESSAGE_MANAGE)))
            return true;

        return false;
    }

    public void dehoist(Member member)
    {
        if(!member.getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE))
            return;

        if(shouldPerformAutomod(member, null))
            return;

        try
        {
            usage.increment(member.getGuild().getIdLong());
            OtherUtil.dehoist(member, OtherUtil.DEHOIST_ORIGINAL[0]);
        }
        catch(Exception ignore) {}
    }

    public void performAutomod(Message message)
    {
        //ignore users with Manage Messages, Kick Members, Ban Members, Manage Server, or anyone the bot can't interact with
        if(shouldPerformAutomod(message.getMember(), message.getTextChannel()))
            return;


        // check the channel for channel-specific settings
        String topic = message.getTextChannel().getTopic();
        boolean preventSpam = true;
        boolean preventInvites = true;

        usage.increment(message.getGuild().getIdLong());

        boolean shouldDelete = false;
        String channelWarning = null;
        int strikeTotal = 0;
        StringBuilder reason = new StringBuilder();


        // prevent referral links

            Matcher m = REF.matcher(message.getContentRaw());
            if(m.find())
            {
                reason.append(", Referral link");
                shouldDelete = true;
            }
            else
            {
                m = BASE_URL.matcher(message.getContentRaw().toLowerCase());
                while(m.find())
                {
                    if(isReferralUrl(m.group(1)))
                    {
                        reason.append(", Referral link");
                        shouldDelete = true;
                        break;
                    }
                }
            }

        // prevent copypastas
        String copypastaName = copypastaResolver.getCopypasta(message.getContentRaw());
        if(copypastaName!=null)
        {
            reason.append(", ").append(copypastaName).append(" copypasta");
            shouldDelete = true;
        }

        // delete the message if applicable
        if(shouldDelete)
        {
            try
            {
                message.delete().reason("Automod").queue(v->{}, f->{});
            }catch(PermissionException ignore){}
        }

        // send a short 'warning' message that self-deletes
        if(channelWarning!=null && message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_WRITE))
        {
            message.getChannel().sendMessage(message.getAuthor().getAsMention() + Emoji.ERROR + " " + channelWarning)
                    .queue(me -> me.delete().queueAfter(2500, TimeUnit.MILLISECONDS, s->{}, f->{}), f->{});
        }

    }

    private void purgeMessages(Guild guild, Predicate<MessageCache.CachedMessage> predicate)
    {
        OtherEvents.messageCache.getMessages(guild, predicate).stream()
                .collect(Collectors.groupingBy(MessageCache.CachedMessage::getTextChannelId)).entrySet().forEach(entry ->
        {
            try
            {
                TextChannel mtc = guild.getTextChannelById(entry.getKey());
                if(mtc != null)
                    mtc.purgeMessagesById(entry.getValue().stream().map(MessageCache.CachedMessage::getId).collect(Collectors.toList()));
            }
            catch(PermissionException ignore) {}
            catch(Exception ex) { LOG.error("Error in purging messages: ", ex); }
        });//*/
        /*vortex.getMessageCache().getMessages(guild, predicate).forEach(m ->
        {

            try
            {
                TextChannel mtc = m.getTextChannel(guild);
                if(mtc!=null)
                    mtc.deleteMessageById(m.getIdLong()).queue(s->{}, f->{});
            }
            catch(PermissionException ignore) {}
        });//*/
    }

    private boolean isReferralUrl(String url)
    {
        for(String reflink: refLinkList)
            if(reflink.equalsIgnoreCase(url))
                return true;
        return false;
    }

    private final static List<String> ZEROWIDTH = Arrays.asList("\u00AD", "\u034F", "\u17B4", "\u17B5", "\u180B", "\u180C", "\u180D",
            "\u180E", "\u200B", "\u200C", "\u200D", "\u200E", "\u202A", "\u202C", "\u202D", "\u2060", "\u2061", "\u2062",
            "\u2063", "\u2064", "\u2065", "\u2066", "\u2067", "\u2068", "\u2069", "\u206A", "\u206B", "\u206C", "\u206D",
            "\u206E", "\u206F", "\uFE00", "\uFE01", "\uFE02", "\uFE03", "\uFE04", "\uFE05", "\uFE06", "\uFE07", "\uFE08",
            "\uFE09", "\uFE0A", "\uFE0B", "\uFE0C", "\uFE0D", "\uFE0E", "\uFE0F", "\uFEFF", "\uFFF0", "\uFFF1", "\uFFF2",
            "\uFFF3", "\uFFF4", "\uFFF5", "\uFFF6", "\uFFF7", "\uFFF8");


}