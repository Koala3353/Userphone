package com.general_hello.commands.commands.Logs;

import com.general_hello.commands.commands.Utils.OtherUtil;

import java.util.HashMap;

public class CopypastaResolver
{
    private final HashMap<String, String[]> copypastas = new HashMap<>();

    public void load()
    {
        String[] lines = OtherUtil.readLines("copypastas");
        if(lines.length!=0)
        {
            copypastas.clear();
            String name;
            String[] words;
            for(String line: lines)
            {
                name = line.substring(0, line.indexOf("||")).trim();
                words = line.substring(line.indexOf("||")+2).trim().split("\\s+&&\\s+");
                for(int i=0; i<words.length; i++)
                    words[i] = words[i].trim().toLowerCase();
                copypastas.put(name, words);
            }
        }
    }

    public String getCopypasta(String message)
    {
        String lower = message.toLowerCase();
        boolean contains;
        String[] words;
        for(String name: copypastas.keySet())
        {
            words = copypastas.get(name);
            if(words==null || words.length==0)
                continue;
            contains = true;
            for(String word: words)
            {
                if(!lower.contains(word))
                {
                    contains = false;
                    break;
                }
            }
            if(contains)
                return name;
        }
        return null;
    }
}
