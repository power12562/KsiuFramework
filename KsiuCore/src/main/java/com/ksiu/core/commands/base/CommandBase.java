package com.ksiu.core.commands.base;

import com.ksiu.core.commands.interfaces.ICommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public abstract class CommandBase implements ICommand
{
    private final String _commandName;
    private final String _commandDescription;

    public CommandBase(String name, String description)
    {
        _commandName = name;
        _commandDescription = description;
    }

    @Override
    public String getName()
    {
        return _commandName;
    }

    @Override
    public String getDescription()
    {
        return _commandDescription;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args)
    {
        return Collections.emptyList();
    }

    public abstract boolean onCommand(CommandSender sender, String[] args);
}