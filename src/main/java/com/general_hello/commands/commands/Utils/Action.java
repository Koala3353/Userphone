package com.general_hello.commands.commands.Utils;

public enum Action
{
    NORAIDMODE("",            "\uD83D\uDD13", 15), // 🔓
    PARDON(    "pardoned",    "\uD83C\uDFF3", 14), // 🏳
    RAIDMODE(  "",            "\uD83D\uDD12", 13), // 🔒
    STRIKE(    "",            "\uD83D\uDEA9", 12), // 🚩
    UNMUTE(    "unmuted",     "\uD83D\uDD0A", 11), // 🔊
    UNBAN(     "unbanned",    "\uD83D\uDD27", 10), // 🔧
    BAN(       "banned",      "\uD83D\uDD28",  9), // 🔨
    TEMPBAN(   "tempbanned",  "\u23F2",        8), // ⏲
    SOFTBAN(   "softbanned",  "\uD83C\uDF4C",  7), // 🍌
    KICK(      "kicked",      "\uD83D\uDC62",  6), // 👢
    MUTE(      "muted",       "\uD83D\uDD07",  5), // 🔇
    TEMPMUTE(  "tempmuted",   "\uD83E\uDD10",  4), // 🤐
    WARN(      "warned",      "\uD83D\uDDE3",  3), // 🗣
    CLEAN(     "cleaned",     "\uD83D\uDDD1",  2), // 🗑
    DELETE(    "deleted",     "\uD83D\uDDD1",  1), // 🗑
    NONE(      "did not act", "\uD83D\uDE36",  0); // 😶

    private final String verb;
    private final String emoji;
    private final int bit;

    private Action(String verb, String emoji, int bit)
    {
        this.verb = verb;
        this.emoji = emoji;
        this.bit = bit;
    }

    public String getVerb()
    {
        return verb;
    }

    public String getEmoji()
    {
        return emoji;
    }

    public int getBit()
    {
        return bit;
    }

    public static Action fromBit(int bit)
    {
        for(Action a: values())
            if(a.bit == bit)
                return a;
        return null;
    }
}
