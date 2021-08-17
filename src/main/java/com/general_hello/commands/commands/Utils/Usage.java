package com.general_hello.commands.commands.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Usage
{
    private final HashMap<Long,Integer> map = new HashMap<>();

    public void increment(long key)
    {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

    public Map<Long,Integer> getMap()
    {
        return map;
    }

    public List<Entry<Long,Integer>> higher(int val)
    {
        return map.entrySet().stream().filter(e -> e.getValue() >= val).collect(Collectors.toList());
    }
}