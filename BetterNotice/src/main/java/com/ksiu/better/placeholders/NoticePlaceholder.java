package com.ksiu.better.placeholders;

import kr.toxicity.hud.api.placeholder.HudPlaceholder;
import kr.toxicity.hud.api.player.HudPlayer;
import kr.toxicity.hud.api.update.UpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NoticePlaceholder implements HudPlaceholder<String>, CommandExecutor, TabCompleter
{
    public String NoticePopupName;
    private String _notice = "";

    public NoticePlaceholder(String noticePopupName)
    {
        NoticePopupName = noticePopupName;
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
        if (!sender.isOp())
        {
            sender.sendMessage("이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        if (args.length == 0)
        {
            sender.sendMessage("/공지 [내용]");
            return true;
        }

        String notice = args[0];
        if (args.length == 1 && notice.equals("숨기기"))
        {
            Bukkit.dispatchCommand(sender, String.format("hud popup hide all %s", NoticePopupName));
        }
        else
        {
            _notice = String.join(" ", args);
            Bukkit.dispatchCommand(sender, String.format("hud popup show all %s", NoticePopupName));
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
