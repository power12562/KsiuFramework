package com.ksiu.better.placeholders;

import kr.toxicity.hud.api.placeholder.HudPlaceholder;
import kr.toxicity.hud.api.player.HudPlayer;
import kr.toxicity.hud.api.update.UpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NoticePlaceholder implements HudPlaceholder<String>, CommandExecutor, TabCompleter
{
    private final String _noticePopupName;
    private String _notice = "";

    public NoticePlaceholder(String noticePopupName)
    {
        _noticePopupName = noticePopupName;
    }

    @Override
    public @NotNull Function<HudPlayer, String> invoke(@NotNull @Unmodifiable List<String> args, @NotNull UpdateEvent reason)
    {
        return player ->
        {
            return _notice;
        };
    }

    @Override
    public int getRequiredArgsLength()
    {
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage("/공지 [내용]");
            return true;
        }

        String notice = args[0];
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        if (args.length == 1 && notice.equals("숨기기"))
        {
            Bukkit.dispatchCommand(console, String.format("hud popup hide all %s", _noticePopupName));
        }
        else
        {
            _notice = String.join(" ", args);
            Bukkit.dispatchCommand(console, String.format("hud popup show all %s", _noticePopupName));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args)
    {
        if (args.length == 1)
        {
            String input = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();
            if ("숨기기".startsWith(input))
            {
                completions.add("숨기기");
            }
            return completions;
        }

        return List.of();
    }
}
