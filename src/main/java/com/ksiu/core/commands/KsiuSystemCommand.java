package com.ksiu.core.commands;

import com.ksiu.core.KsiuCore;
import com.ksiu.core.commands.base.CommandBase;
import com.ksiu.core.commands.base.OpCommandBase;
import com.ksiu.core.commands.container.KsiuCommandList;
import com.ksiu.core.commands.interfaces.ICommandBundle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class KsiuSystemCommand implements ICommandBundle
{
    private final KsiuCore _ksiuCore;
    private final KsiuCommandList _commandList;

    public KsiuSystemCommand(KsiuCore ksiuCore)
    {
        _ksiuCore = ksiuCore;
        _commandList = new KsiuCommandList("system");
        _commandList.put(new VersionCommand());
        _commandList.put(new StatusCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args)
    {
        _commandList.onCommand(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args)
    {
        return _commandList.onTabComplete(sender, args);
    }

    private class VersionCommand extends CommandBase
    {
        public VersionCommand()
        {
            super("version", "코어 플러그인 버전 정보를 확인합니다.");
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args)
        {
            sender.sendMessage(KsiuCore.getPrefixTextBuilder()
                    .append(Component.text(_ksiuCore.getVersion()))
                    .build());
            return true;
        }
    }

    private static class StatusCommand extends OpCommandBase
    {
        public StatusCommand()
        {
            super("Status", "서버 리소스 상태를 확인합니다.");
        }

        @Override
        public boolean onOpCommand(CommandSender sender, String[] args)
        {
            Runtime runtime = Runtime.getRuntime();

            long max = runtime.maxMemory() / 1024 / 1024;
            long total = runtime.totalMemory() / 1024 / 1024;
            long free = runtime.freeMemory() / 1024 / 1024;
            long used = total - free;

            sender.sendMessage(KsiuCore.getPrefixTextBuilder()
                    .append(Component.text("System Resource Status", NamedTextColor.WHITE))
                    .build());

            sender.sendMessage(Component.text(" > ", NamedTextColor.AQUA)
                    .append(Component.text("Memory: ", NamedTextColor.YELLOW))
                    .append(Component.text(used + "MB / " + max + "MB", NamedTextColor.WHITE)));

            double tps = Bukkit.getTPS()[0];
            sender.sendMessage(Component.text(" > ", NamedTextColor.AQUA)
                    .append(Component.text("TPS: ", NamedTextColor.YELLOW))
                    .append(Component.text(String.format("%.2f", tps), NamedTextColor.WHITE)));

            return true;
        }
    }

}
