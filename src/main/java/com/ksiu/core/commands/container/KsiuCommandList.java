package com.ksiu.core.commands.container;

import com.ksiu.core.KsiuCore;
import com.ksiu.core.commands.interfaces.ICommand;
import com.ksiu.core.commands.interfaces.ICommandBundle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.*;

public final class KsiuCommandList implements ICommandBundle
{
    private final Map<String, ICommand> _commands = new HashMap<>();
    private final String _moduleName;

    public KsiuCommandList(String moduleName)
    {
        _moduleName = moduleName;
    }

    public void put(ICommand command)
    {
        _commands.put(command.getName().toLowerCase(), command);
    }

    public Map<String, ICommand> getCommands()
    {
        return Collections.unmodifiableMap(_commands);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            showFixedHelp(sender);
            return true;
        }

        String subLabel = args[0].toLowerCase();
        ICommand command = _commands.get(subLabel);
        if (command == null)
        {
            sender.sendMessage(KsiuCore.getErrorTextBuilder()
                    .append(Component.text("알 수 없는 명령어입니다: " + subLabel, NamedTextColor.WHITE))
                    .build());
            return true;
        }
        return command.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
    }

    private void showFixedHelp(CommandSender sender)
    {
        sender.sendMessage(KsiuCore.getPrefixTextBuilder()
                .append(Component.text(_moduleName + " 모듈 명령어 목록", NamedTextColor.WHITE))
                .build());

        for (ICommand cmd : _commands.values())
        {
            sender.sendMessage(Component.text(" > ", NamedTextColor.AQUA)
                    .append(Component.text(cmd.getName(), NamedTextColor.YELLOW))
                    .append(Component.text(" : ", NamedTextColor.WHITE))
                    .append(Component.text(cmd.getDescription(), NamedTextColor.GRAY)));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], _commands.keySet(), completions);
            Collections.sort(completions);
            return completions;
        }

        if (args.length > 1)
        {
            ICommand command = _commands.get(args[0].toLowerCase());
            if (command != null)
            {
                return command.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return Collections.emptyList();
    }
}