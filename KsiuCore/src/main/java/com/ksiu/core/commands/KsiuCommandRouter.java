package com.ksiu.core.commands;

import com.ksiu.core.KsiuCore;
import com.ksiu.core.commands.interfaces.ICommandBundle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

public final class KsiuCommandRouter extends BukkitCommand
{
    private final Map<String, ICommandBundle> _commandBundles = new HashMap<>();
    private final JavaPlugin _plugin;

    public KsiuCommandRouter(JavaPlugin plugin)
    {
        super("ksiu");
        _plugin = plugin;
    }

    public final void registerCommandBundle(String name, ICommandBundle bundle)
    {
        String lowerName = name.toLowerCase();
        if (_commandBundles.containsKey(lowerName))
        {
            _plugin.getLogger().warning("[warning]\"" + lowerName + "\"은 중복된 서브 플러그인입니다.");
            return;
        }
        _commandBundles.put(lowerName, bundle);
    }

    @Override
    public @NonNull List<String> tabComplete(@NonNull CommandSender sender, @NonNull String alias, String[] args) throws IllegalArgumentException
    {
        if (args.length == 1)
        {
            List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], _commandBundles.keySet(), completions);
            Collections.sort(completions);
            return completions;
        }

        String targetModule = args[0].toLowerCase();
        ICommandBundle bundle = _commandBundles.get(targetModule);
        if (bundle == null)
        {
            return Collections.emptyList();
        }
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return bundle.onTabComplete(sender, subArgs);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage(KsiuCore.getPrefixTextBuilder()
                    .append(Component.text("현재 로드된 모듈 목록:", NamedTextColor.WHITE))
                    .build());
            for (String moduleName : _commandBundles.keySet())
            {
                sender.sendMessage(Component.text(" > ", NamedTextColor.AQUA)
                        .append(Component.text(moduleName, NamedTextColor.YELLOW)));
            }
            return true;
        }

        String targetModule = args[0].toLowerCase();
        ICommandBundle bundle = _commandBundles.get(targetModule);
        if (bundle == null)
        {
            sender.sendMessage(KsiuCore.getErrorTextBuilder()
                    .append(Component.text("알 수 없는 모듈입니다: " + targetModule, NamedTextColor.WHITE))
                    .build());
            return true;
        }
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return bundle.onCommand(sender, subArgs);
    }
}
