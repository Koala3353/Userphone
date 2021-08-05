package com.general_hello.commands.Database;

public interface DatabaseManager {
    DatabaseManager INSTANCE = new SQLiteDataSource();

    String getPrefix(long guildId);
    String getName(long userId);
    String getProfilePictureLink(long userId);
    void setPrefix(long guildId, String newPrefix);
    void newInfo(long userId, String userName, String profilePictureLink);
    void setProfilePictureLink(long userId, String link);
    void setName(long userId, String name);
}