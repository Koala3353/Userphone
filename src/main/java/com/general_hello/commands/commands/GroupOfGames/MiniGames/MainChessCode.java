package com.general_hello.commands.commands.GroupOfGames.MiniGames;

import com.general_hello.commands.commands.Emoji.EmojiObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class MainChessCode {
    //black
    public static EmojiObject emojiObjectPawn = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.BPAWN);
    public static EmojiObject emojiObjectKing = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.BKING);
    public static EmojiObject emojiObjectQueen = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.BQUEEN);
    public static EmojiObject emojiObjectRook = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.BROOK);
    public static EmojiObject emojiObjectKnight = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.BKNIGHT);
    public static EmojiObject emojiObjectBishop = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.BBISHOP);

    //white
    public static EmojiObject wemojiObjectPawn = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.WPAWN);
    public static EmojiObject wemojiObjectKing = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.WKING);
    public static EmojiObject wemojiObjectQueen = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.WQUEEN);
    public static EmojiObject wemojiObjectRook = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.WROOK);
    public static EmojiObject wemojiObjectKnight = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.WKNIGHT);
    public static EmojiObject wemojiObjectBishop = com.general_hello.commands.commands.Emoji.Emoji.customEmojiToEmote(com.general_hello.commands.commands.Emoji.Emoji.WBISHOP);

    public static void loadBoard(ButtonClickEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setDescription("----------------------------------");

        event.getChannel().sendMessageEmbeds(embedBuilder.build()).setActionRows(
                //black
                ActionRow.of(
                        Button.primary(event.getUser().getId() + ":bpawn",
                                Emoji.fromEmote(emojiObjectPawn.getEmojiName(), emojiObjectPawn.getEmojiID(), emojiObjectPawn.isAnimated())),
                        Button.primary(event.getUser().getId() + ":brook",
                                Emoji.fromEmote(emojiObjectRook.getEmojiName(), emojiObjectRook.getEmojiID(), emojiObjectRook.isAnimated())),
                        Button.primary(event.getUser().getId() + ":bknight",
                                Emoji.fromEmote(emojiObjectKnight.getEmojiName(), emojiObjectKnight.getEmojiID(), emojiObjectKnight.isAnimated())),
                        Button.primary(event.getUser().getId() + ":bbishop",
                                Emoji.fromEmote(emojiObjectBishop.getEmojiName(), emojiObjectBishop.getEmojiID(), emojiObjectBishop.isAnimated())),
                        Button.primary(event.getUser().getId() + ":bqueen",
                                Emoji.fromEmote(emojiObjectQueen.getEmojiName(), emojiObjectQueen.getEmojiID(), emojiObjectQueen.isAnimated()))),
                //white
                ActionRow.of(Button.secondary(event.getUser().getId() + ":wpawn",
                        Emoji.fromEmote(wemojiObjectPawn.getEmojiName(), wemojiObjectPawn.getEmojiID(), emojiObjectPawn.isAnimated())).asDisabled(),
                        Button.secondary(event.getUser().getId() + ":wrook",
                                Emoji.fromEmote(wemojiObjectRook.getEmojiName(), wemojiObjectRook.getEmojiID(), wemojiObjectRook.isAnimated())).asDisabled(),
                        Button.secondary(event.getUser().getId() + ":wknight",
                                Emoji.fromEmote(wemojiObjectKnight.getEmojiName(), wemojiObjectKnight.getEmojiID(), wemojiObjectKnight.isAnimated())).asDisabled(),
                        Button.secondary(event.getUser().getId() + ":wbishop",
                                Emoji.fromEmote(wemojiObjectBishop.getEmojiName(), wemojiObjectBishop.getEmojiID(), wemojiObjectBishop.isAnimated())).asDisabled(),
                        Button.secondary(event.getUser().getId() + ":wqueen",
                                Emoji.fromEmote(wemojiObjectQueen.getEmojiName(), wemojiObjectQueen.getEmojiID(), wemojiObjectQueen.isAnimated())).asDisabled()),
                //king
                ActionRow.of(
                        Button.danger(event.getUser().getId() + ":bking",
                                Emoji.fromEmote(emojiObjectKing.getEmojiName(), emojiObjectKing.getEmojiID(), emojiObjectKing.isAnimated())),

                        Button.danger(event.getUser().getId() + ":wking",
                                Emoji.fromEmote(wemojiObjectKing.getEmojiName(), wemojiObjectKing.getEmojiID(), wemojiObjectKing.isAnimated())).asDisabled())).queue();
    }
}
