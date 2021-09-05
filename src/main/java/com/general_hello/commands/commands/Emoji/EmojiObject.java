package com.general_hello.commands.commands.Emoji;

public class EmojiObject {
    private final String emojiName;
    private final boolean isAnimated;
    private final long emojiID;

    public EmojiObject(String emojiName, boolean isAnimated, long emojiID) {
        this.emojiName = emojiName;
        this.isAnimated = isAnimated;
        this.emojiID = emojiID;
    }

    public String getEmojiName() {
        return emojiName;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public long getEmojiID() {
        return emojiID;
    }
}
