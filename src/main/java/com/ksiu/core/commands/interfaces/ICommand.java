package com.ksiu.core.commands.interfaces;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand
{
    String getName();

    String getDescription();

    boolean onCommand(CommandSender sender, String[] args);

    List<String> onTabComplete(CommandSender sender, String[] args);
}

