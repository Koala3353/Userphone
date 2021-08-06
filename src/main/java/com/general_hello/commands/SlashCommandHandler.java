package com.general_hello.commands;

import com.general_hello.commands.commands.RankingSystem.SlashCommandContext;
import com.general_hello.commands.commands.RankingSystem.SlashRankCommand;
import com.general_hello.commands.commands.RankingSystem.XPAlertCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SlashCommandHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandHandler.class);

    public final List<SlashCommand> registeredCommands;
    public final ConcurrentHashMap<Long, List<SlashCommand>> registeredGuildCommands;
    private CommandListUpdateAction commandUpdateAction;

    public SlashCommandHandler()
    {
        registeredCommands = Collections.synchronizedList(new ArrayList<>());
        registeredGuildCommands = new ConcurrentHashMap<>();
    }

    public void initialize()
    {
        commandUpdateAction = Bot.jda.updateCommands();
        registerAllCommands();
    }

    public void registerAllCommands()
    {
        registerCommand(new SlashRankCommand());
        registerCommand(new XPAlertCommand());
    }

    public void updateCommands(Consumer<List<Command>> success, Consumer<Throwable> failure)
    {
        commandUpdateAction.queue(success, failure);
        for (Map.Entry<Long, List<SlashCommand>> entrySet : registeredGuildCommands.entrySet())
        {
            Long guildID = entrySet.getKey();
            List<SlashCommand> slashCommands = entrySet.getValue();
            if (guildID == null || slashCommands == null) continue;
            if (slashCommands.isEmpty()) continue;
            Guild guild = Bot.jda.getShardManager().getGuildById(guildID);
            if (guild == null) continue;
            CommandListUpdateAction guildCommandUpdateAction = guild.updateCommands();
            for (SlashCommand cmd : slashCommands)
            {
                guildCommandUpdateAction = guildCommandUpdateAction.addCommands(cmd.getCommandData());
            }
            if (slashCommands.size() > 0) guildCommandUpdateAction.queue();
        }
    }

    private void registerCommand(SlashCommand command)
    {
        if (!command.isGlobal())
        {
            if (command.getEnabledGuilds() == null) return;
            if (command.getEnabledGuilds().isEmpty()) return;
            for (Long guildID : command.getEnabledGuilds())
            {
                Guild guild = Bot.jda.getShardManager().getGuildById(guildID);
                if (guild == null) continue;
                List<SlashCommand> alreadyRegistered = registeredGuildCommands.containsKey(guildID) ? registeredGuildCommands.get(guildID) : new ArrayList<>();
                alreadyRegistered.add(command);
                if (registeredGuildCommands.containsKey(guildID))
                {
                    registeredGuildCommands.replace(guildID, alreadyRegistered);
                } else
                {
                    registeredGuildCommands.put(guildID, alreadyRegistered);
                }
            }
            return;
        }

        commandUpdateAction.addCommands(command.getCommandData());
        registeredCommands.add(command);
    }


    public void handleSlashCommand(@NotNull SlashCommandEvent event, @Nullable Member member)
    {
        Runnable r = () ->
        {
            boolean foundCommand = false;
            try
            {
                if (event.getGuild() != null)
                {
                    Guild guild = event.getGuild();
                    long guildID = guild.getIdLong();
                    if (registeredGuildCommands.containsKey(guildID))
                    {
                        List<SlashCommand> guildOnlySlashcommands = registeredGuildCommands.get(guildID);
                        for (SlashCommand cmd : guildOnlySlashcommands)
                        {
                            if (cmd == null) continue;
                            if (cmd.getCommandName() == null) continue;
                            if (cmd.getCommandName().equalsIgnoreCase(event.getName()))
                            {
                                InteractionHook hook = event.getHook();
                                List<Permission> neededPermissions = cmd.getRequiredUserPermissions();
                                List<Permission> neededBotPermissions = cmd.getRequiredBotPermissions();
                                if (neededPermissions != null)
                                {
                                    for (Permission permission : neededPermissions)
                                    {
                                        if (!member.hasPermission(permission))
                                        {
                                            event.deferReply(true)
                                                    .flatMap(v -> hook.sendMessage("You don't have any permissions to do this slash command"))
                                                    .queue();
                                            return;
                                        }
                                    }
                                }

                                if (neededBotPermissions != null)
                                {
                                    for (Permission permission : neededBotPermissions)
                                    {
                                        if (!event.getGuild().getSelfMember().hasPermission(permission))
                                        {
                                            event.deferReply(true)
                                                    .flatMap(v -> hook.sendMessage("The bot lacks permission of " + permission.getName()))
                                                    .queue();
                                            return;
                                        }
                                    }
                                }
                                SlashCommandContext ctx = new SlashCommandContext(event);
                                cmd.executeCommand(event, member, ctx);
                                return;
                            }
                        }
                    }
                }
                for (SlashCommand cmd : registeredCommands)
                {
                    if (cmd == null) continue;
                    if (cmd.getCommandName() == null) continue;
                    if (cmd.getCommandName().equalsIgnoreCase(event.getName()))
                    {
                        foundCommand = true;
                        if (member == null && !cmd.isRunnableInDM())
                        {
                            event.reply("The command cannot be ran in DM's").setEphemeral(true).queue();
                            return;
                        }
                        InteractionHook hook = event.getHook();
                        List<Permission> neededPermissions = cmd.getRequiredUserPermissions();
                        List<Permission> neededBotPermissions = cmd.getRequiredBotPermissions();
                        if (member != null)
                        {
                            if (neededPermissions != null)
                            {
                                for (Permission permission : neededPermissions)
                                {
                                    if (!member.hasPermission(permission))
                                    {
                                        event.deferReply(true)
                                                .flatMap(v -> hook.sendMessage("You don't have the " + permission.getName()))
                                                .queue();
                                        return;
                                    }
                                }
                            }

                            if (neededBotPermissions != null)
                            {
                                for (Permission permission : neededBotPermissions)
                                {
                                    if (!event.getGuild().getSelfMember().hasPermission(permission))
                                    {
                                        event.deferReply(true)
                                                .flatMap(v -> hook.sendMessage("The bot lacks " + permission.getName() + " to do this command!"))
                                                .queue();
                                        return;
                                    }
                                }
                            }
                        }
                        SlashCommandContext ctx = new SlashCommandContext(event);
                        cmd.executeCommand(event, member, ctx);
                    }
                }
                if (!foundCommand && member != null)
                    event.reply("The commands is either disabled or not found!").setEphemeral(true).queue();

            } catch (Exception e)
            {
                LOGGER.error("Could not execute slash-command", e);
                StringBuilder path = new StringBuilder("/"+event.getCommandPath().replace("/", " "));
                for(OptionMapping option : event.getOptions())
                {
                    path.append(" ").append(option.getName()).append(":").append("`").append(option.getAsString()).append("`");
                }
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle("An error occurred while executing a slash-command!")
                        .addField("Guild", (event.getGuild() == null ? "None (Direct message)" : event.getGuild().getIdLong()+" ("+event.getGuild().getName()+")"),true)
                        .addField("User", event.getUser().getAsMention()+" ("+event.getUser().getAsTag()+")", true)
                        .addField("Command", path.toString(), false)
                        .setDescription("```\n"+ e.getLocalizedMessage() +"\n```")
                        .setColor(Color.RED);
                event.getJDA().openPrivateChannelById(Config.get("owner_id"))
                        .flatMap(c -> c.sendMessageEmbeds(builder.build()))
                        .queue();

                event.getJDA().openPrivateChannelById(Config.get("owner_id_partner"))
                        .flatMap(c -> c.sendMessageEmbeds(builder.build()))
                        .queue();

                event.reply("An unknown error occured! The owner of the bot has been notified of this!").setEphemeral(true).queue(s -> {}, ex -> {});
            }
        };
    }
}