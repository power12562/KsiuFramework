package com.ksiu.core;

import com.ksiu.core.builders.TextComponentBuilder;
import com.ksiu.core.commands.KsiuCommandRouter;
import com.ksiu.core.commands.KsiuSystemCommand;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

public final class KsiuCore extends JavaPlugin
{
    private static KsiuCore instance;

    public static KsiuCore getInstance()
    {
        return instance;
    }

    private KsiuCommandRouter _commandRouter;

    public static String getVersion()
    {
        return "1.0.1";
    }

    public static String getPropertiesPath()
    {
        return "Ksiu";
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

    @Override
    public void onEnable()
    {
        instance = this;
        getLogger().info("=== KsiuCore Kernel 가동 ===");
        _commandRouter = new KsiuCommandRouter(this);
        Class<?> serverClass = getServer().getClass();
        try
        {
            Field field = serverClass.getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(getServer());
            commandMap.register("ksiu", _commandRouter);
        }
        catch (Exception ex)
        {
            getLogger().warning("[warning] Ksiu 커맨드 등록에 실패하였습니다.");
        }
        _commandRouter.registerCommandBundle("system", new KsiuSystemCommand(this));
    }

    @Override
    public void onDisable()
    {
        getLogger().info("=== KsiuCore Kernel 종료 ===");
    }

    public final KsiuCommandRouter getCommandRouter()
    {
        return _commandRouter;
    }

    public static TextComponentBuilder getPrefixTextBuilder()
    {
        return TextComponentBuilder.newBuilder().content("[Ksiu] ").color(NamedTextColor.AQUA);
    }

    public static TextComponentBuilder getWarningTextBuilder()
    {
        return TextComponentBuilder.newBuilder().content("[Ksiu:Warning] ").color(NamedTextColor.GOLD);
    }

    public static TextComponentBuilder getErrorTextBuilder()
    {
        return TextComponentBuilder.newBuilder().content("[Ksiu:Error] ").color(NamedTextColor.RED);
    }
}
