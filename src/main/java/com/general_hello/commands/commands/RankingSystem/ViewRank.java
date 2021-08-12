package com.general_hello.commands.commands.RankingSystem;

import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import com.general_hello.commands.commands.Utils.ErrorUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class ViewRank implements ICommand
{
    @Override
    public void handle(CommandContext ctx) throws InterruptedException, IOException, SQLException {
        try{
            ByteArrayOutputStream baos = LevelPointManager.getLevelPointCard(ctx.getMember()).getByteArrayOutputStream();
            ctx.getEvent().getChannel().sendFile(baos.toByteArray(), "stats.png").queue();
        }
        catch(Exception e){
            ErrorUtils.error(ctx, e);
        }
    }

    @Override
    public String getName() {
        return "rank";
    }

    @Override
    public String getHelp(String prefix) {
        return "Shows your rank!";
    }
}
