package com.ksiu.better;

import com.ksiu.better.placeholders.NoticePlaceholder;
import kr.toxicity.hud.api.BetterHud;
import kr.toxicity.hud.api.BetterHudAPI;
import kr.toxicity.hud.api.manager.PlaceholderManager;
import kr.toxicity.hud.api.placeholder.PlaceholderContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class BetterNotice extends JavaPlugin
{
    private NoticePlaceholder _noticePlaceholder;
    private String _noticePopupName;

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        readSettings();
        BetterHud betterHud = BetterHudAPI.inst();
        PlaceholderManager placeholders = betterHud.getPlaceholderManager();
        PlaceholderContainer<String> stringContainer = placeholders.getStringContainer();

        _noticePlaceholder = new NoticePlaceholder(_noticePopupName);
        stringContainer.addPlaceholder("better_notice", _noticePlaceholder);
        registerCommand(this, "공지", _noticePlaceholder);
        registerCommand(this, "bnreload", new bnreloadCommand(this));
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic

    }

    String getNoticePopupName()
    {
        return _noticePopupName;
    }

    void readSettings()
    {
        final String config = "config";
        Properties properties = readProperties(config);
        _noticePopupName = properties.getProperty("notice-popup-name");
        if (_noticePopupName == null)
        {
            _noticePopupName = "notice_popup";
            properties.put("notice-popup-name", _noticePopupName);
            try
            {
                writeProperties(config, properties);
            }
            catch (IOException e)
            {
                getLogger().warning(e.toString());
            }
        }

        if (_noticePlaceholder != null)
        {
            _noticePlaceholder.NoticePopupName = _noticePopupName;
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

    private static class bnreloadCommand implements CommandExecutor
    {
        private final BetterNotice _betterNotice;

        public bnreloadCommand(BetterNotice betterNotice)
        {
            _betterNotice = betterNotice;
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args)
        {
            if (!sender.isOp())
            {
                sender.sendMessage("이 명령어를 사용할 권한이 없습니다.");
                return true;
            }

            _betterNotice.readSettings();
            sender.sendMessage("공지 팝업: " + _betterNotice.getNoticePopupName());
            return true;
        }
    }
}
