package com.ksiu.core;

import com.ksiu.core.builders.TextComponentBuilder;
import com.ksiu.core.commands.KsiuCommandRouter;
import com.ksiu.core.commands.KsiuSystemCommand;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

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
        return "1.0.0";
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
        return TextComponentBuilder.newBuilder().content("[KsiuCore] ").color(NamedTextColor.AQUA);
    }

    public static TextComponentBuilder getErrorTextBuilder()
    {
        return TextComponentBuilder.newBuilder().content("[KsiuCore:Error] ").color(NamedTextColor.RED);
    }
}
