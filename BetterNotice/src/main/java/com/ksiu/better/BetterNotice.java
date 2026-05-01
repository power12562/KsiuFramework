package com.ksiu.better;

import kr.toxicity.hud.api.BetterHud;
import kr.toxicity.hud.api.BetterHudAPI;
import kr.toxicity.hud.api.manager.PlaceholderManager;
import kr.toxicity.hud.api.placeholder.HudPlaceholder;
import kr.toxicity.hud.api.placeholder.PlaceholderContainer;
import kr.toxicity.hud.api.player.HudPlayer;
import kr.toxicity.hud.api.update.UpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public final class BetterNotice extends JavaPlugin
{
    private NoticeCommand _noticeCommand;

    private String _noticePopupName;
    private long _noticeDuration = 1;
    private int _noticeLine1Len = 0;
    private int _noticeLine2Len = 0;

    private final Queue<String> _noticeQueue = new ArrayDeque<>();
    private String _noticeLine1 = "";
    private String _noticeLine2 = "";
    private String _noticeLine3 = "";

    private BukkitRunnable _noticeRunnable;

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        readConfig();
        BetterHud betterHud = BetterHudAPI.inst();
        PlaceholderManager placeholders = betterHud.getPlaceholderManager();
        PlaceholderContainer<String> stringContainer = placeholders.getStringContainer();

        stringContainer.addPlaceholder("better_notice_line1", new HudPlaceholder<String>()
        {
            @Override
            public @NotNull Function<HudPlayer, String> invoke(@NotNull @Unmodifiable List<String> args, @NotNull UpdateEvent reason)
            {
                return hudPlayer -> _noticeLine1;
            }

            @Override
            public int getRequiredArgsLength()
            {
                return 0;
            }
        });
        stringContainer.addPlaceholder("better_notice_line2", new HudPlaceholder<String>()
        {
            @Override
            public @NotNull Function<HudPlayer, String> invoke(@NotNull @Unmodifiable List<String> args, @NotNull UpdateEvent reason)
            {
                return hudPlayer -> _noticeLine2;
            }

            @Override
            public int getRequiredArgsLength()
            {
                return 0;
            }
        });
        stringContainer.addPlaceholder("better_notice_line3", new HudPlaceholder<String>()
        {
            @Override
            public @NotNull Function<HudPlayer, String> invoke(@NotNull @Unmodifiable List<String> args, @NotNull UpdateEvent reason)
            {
                return hudPlayer -> _noticeLine3;
            }

            @Override
            public int getRequiredArgsLength()
            {
                return 0;
            }
        });

        _noticeCommand = new NoticeCommand(_noticePopupName);
        registerCommand(this, "공지", _noticeCommand);
        registerCommand(this, "bnreload", new bnreloadCommand());
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        if (_noticeRunnable != null)
        {
            _noticeRunnable.cancel();
        }
    }

    private void readConfig()
    {
        final String config = "config";
        Properties properties = readProperties(config);
        _noticePopupName = properties.getProperty("notice-popup-name");
        if (_noticePopupName == null)
        {
            _noticePopupName = "notice_popup";
            properties.put("notice-popup-name", _noticePopupName);
        }
        if (_noticeCommand != null)
        {
            _noticeCommand.NoticePopupName = _noticePopupName;
        }

        try
        {
            _noticeDuration = Long.parseLong(properties.getProperty("notice-duration"));
            _noticeDuration = Long.max(_noticeDuration, 20L);
        }
        catch (Exception e)
        {
            _noticeDuration = 200;
            properties.put("notice-duration", String.valueOf(_noticeDuration));
        }

        try
        {
            _noticeLine1Len = Integer.parseInt(properties.getProperty("notice-line1-length"));
            _noticeLine1Len = Integer.max(_noticeLine1Len, 0);
            _noticeLine2Len = Integer.parseInt(properties.getProperty("notice-line2-length"));
            _noticeLine2Len = Integer.max(_noticeLine2Len, 0);
        }
        catch (Exception e)
        {
            _noticeLine1Len = 0;
            _noticeLine2Len = 0;
            properties.put("notice-line1-length", String.valueOf(_noticeLine1Len));
            properties.put("notice-line2-length", String.valueOf(_noticeLine2Len));
        }

        try
        {
            writeProperties(config, properties);
        }
        catch (IOException e)
        {
            getLogger().warning(e.toString());
        }
    }

    public void putNotice(String notice)
    {
        _noticeQueue.offer(notice);
        if (_noticeRunnable == null)
        {
            _noticeRunnable = new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    String notice = _noticeQueue.poll();
                    if (notice == null)
                    {
                        cancel();
                        return;
                    }

                    int noticeLen = notice.length();
                    _noticeLine1 = "";
                    _noticeLine2 = "";
                    _noticeLine3 = "";
                    if (_noticeLine1Len <= 0 || noticeLen <= _noticeLine1Len)
                    {
                        _noticeLine1 = notice;
                    }
                    else if (_noticeLine2Len <= 0 || noticeLen <= _noticeLine2Len)
                    {
                        _noticeLine2 = notice;
                    }
                    else
                    {
                        _noticeLine3 = notice;
                    }
                }

                @Override
                public void cancel()
                {
                    getLogger().info("Notice Task End.");
                    _noticeLine1 = "";
                    _noticeLine2 = "";
                    _noticeLine3 = "";
                    _noticeRunnable = null;
                    _noticeQueue.clear();
                    super.cancel();
                }

            };
            _noticeRunnable.runTaskTimer(this, 0L, _noticeDuration);
        }
    }

    public static void registerCommand(JavaPlugin plugin, String name, CommandExecutor executor)
    {
        PluginCommand cmd = plugin.getCommand(name);
        if (cmd != null)
        {
            cmd.setExecutor(executor);
        }
    }

    public static String getPropertiesPath()
    {
        return Path.of("plugins", "BetterNotice").toString();
    }

    public static Properties readProperties(String fileName)
    {
        Properties prop = new Properties();
        File file = new File(getPropertiesPath(), fileName + ".properties");
        if (!file.exists())
        {
            return prop; // 파일이 없으면 빈 객체 반환
        }

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8))
        {
            prop.load(reader);
        }
        catch (IOException ex)
        {
            prop.clear();
            return prop;
        }
        return prop;
    }

    public static void writeProperties(String fileName, Properties prop) throws IOException
    {
        File dir = new File(getPropertiesPath());
        if (!dir.exists())
        {
            dir.mkdirs(); // 폴더가 없으면 생성
        }
        File file = new File(dir, fileName + ".properties");
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8))
        {
            prop.store(writer, "Ksiu Configuration System - Last Updated: " + System.currentTimeMillis());
        }
    }

    private class bnreloadCommand implements CommandExecutor
    {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args)
        {
            if (!sender.isOp())
            {
                sender.sendMessage("이 명령어를 사용할 권한이 없습니다.");
                return true;
            }

            readConfig();
            sender.sendMessage("공지 팝업 이름: " + _noticePopupName);
            sender.sendMessage("공지 지속 시간: " + _noticeDuration);
            return true;
        }
    }

    private class NoticeCommand implements CommandExecutor, TabCompleter
    {
        public String NoticePopupName;

        public NoticeCommand(String noticePopupName)
        {
            NoticePopupName = noticePopupName;
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

            String notice0 = args[0];
            if (args.length == 1 && notice0.equals("숨기기"))
            {
                if (_noticeRunnable != null)
                {
                    _noticeRunnable.cancel();
                }
                Bukkit.dispatchCommand(sender, String.format("hud popup hide all %s", NoticePopupName));
            }
            else
            {
                String notice = String.join(" ", args);
                putNotice(notice);
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

}
