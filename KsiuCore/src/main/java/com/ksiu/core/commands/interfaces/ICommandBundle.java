package com.ksiu.core.commands.interfaces;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommandBundle
{
    boolean onCommand(CommandSender sender, String[] args);

    List<String> onTabComplete(CommandSender sender, String[] args);
}
