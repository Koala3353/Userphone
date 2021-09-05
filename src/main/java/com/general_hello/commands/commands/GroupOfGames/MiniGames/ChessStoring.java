package com.general_hello.commands.commands.GroupOfGames.MiniGames;

import com.github.bhlangonijr.chesslib.Board;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class ChessStoring {
    public static HashMap<User, Board> userToBoard = new HashMap<>();
    public static HashMap<User, User> userToUser = new HashMap<>();
}
