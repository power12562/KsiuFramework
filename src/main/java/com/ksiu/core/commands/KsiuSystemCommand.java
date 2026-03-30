package com.ksiu.core.commands;

import com.ksiu.core.KsiuCore;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class KsiuSystemCommand implements ICommandBundle
{
    private final KsiuCore _core;

    public KsiuSystemCommand(KsiuCore core)
    {
        _core = core;
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage(_core.getVersion());
            return true;
        }
        sender.sendMessage(NamedTextColor.RED + "[Error]" + NamedTextColor.WHITE + "알 수 없는 명령어입니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args)
    {
        return Collections.emptyList();
    }
}
