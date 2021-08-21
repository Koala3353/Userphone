package com.general_hello.commands.commands.Logs;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class TextUploader
{
    /**
     * Alright, I'm going to write a bit here because I have a feeling that some
     * people will wonder about this. The purpose of this class is to utilize
     * Discord's persistent and encrypted message channels as storage for
     * message logs.
     */
    private final Logger LOG = LoggerFactory.getLogger("Upload");
    private final List<WebhookClient> webhooks = new ArrayList<>();
    private int index = 0;

    public TextUploader(String urls)
    {
        webhooks.add(new WebhookClientBuilder(urls).build());
    }

    public synchronized void upload(String content, String filename, BiConsumer<String,String> done)
    {
        webhooks.get(index % webhooks.size()).send(content.getBytes(StandardCharsets.UTF_8), filename+".txt").whenCompleteAsync((msg, err) ->
        {
            if(msg != null)
            {
                String url = msg.getAttachments().get(0).getUrl();
                done.accept("https://txt.discord.website?txt=" + url.substring(url.indexOf("s/")+2, url.length()-4), url);
            }
            else if(err != null)
            {
                LOG.error("Failed to upload: ", err);
            }
        });
        index++;
    }


    public static interface Result
    {
        public abstract void consume(String view, String download);
    }
}
