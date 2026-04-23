package com.ksiu.core.commands.base;

import com.ksiu.core.KsiuCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public abstract class OpCommandBase extends CommandBase
{
    public OpCommandBase(String name, String description)
    {
        super(name, description);
    }

    @Override
    public final boolean onCommand(CommandSender sender, String[] args)
    {
        if (!sender.isOp())
        {
            sender.sendMessage(KsiuCore.getErrorTextBuilder()
                    .append(Component.text("이 명령어를 사용할 권한이 없습니다.", NamedTextColor.RED))
                    .build());
            return true;
        }
        return onOpCommand(sender, args);
    }

    public abstract boolean onOpCommand(CommandSender sender, String[] args);
}
