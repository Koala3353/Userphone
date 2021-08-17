package com.general_hello.commands.commands.Logs;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Invite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InviteResolver
{
    private final Logger log = LoggerFactory.getLogger(InviteResolver.class);
    private final FixedCache<String,Long> cached = new FixedCache<>(5000);

    public long resolve(String code, JDA jda)
    {
        log.debug("Attempting to resolve " + code);
        if(cached.contains(code))
            return cached.get(code);
        try
        {
            Invite i = Invite.resolve(jda, code).complete(false);
            cached.put(code, i.getGuild().getIdLong());
            return i.getGuild().getIdLong();
        }
        catch(Exception ex)
        {
            cached.put(code, 0L);
            return 0L;
        }
    }
}