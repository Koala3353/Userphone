package com.general_hello.commands.commands.DefaultCommands;

import com.general_hello.commands.SlashCommands.SlashCommandHandler;
import com.general_hello.commands.commands.CommandContext;
import com.general_hello.commands.commands.ICommand;
import com.general_hello.commands.commands.Utils.MCColor;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateSlashCommand implements ICommand
{

    @Override
    public void handle(CommandContext ctx) throws InterruptedException, IOException, SQLException {
        SlashCommandHandler.updateCommands((x) -> System.out.println(MCColor.translate("&aQueued "+x.size()+" commands!")), Throwable::printStackTrace);
    }

    @Override
    public String getName() {
        return "updateslashcommands";
    }

    @Override
    public String getHelp(String prefix) {
        return "Tells Discord to update all commands";
    }
}
