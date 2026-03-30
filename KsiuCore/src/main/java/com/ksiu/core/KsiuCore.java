package com.ksiu.core;

import com.ksiu.core.commands.KsiuCommandRouter;
import com.ksiu.core.commands.KsiuSystemCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class KsiuCore extends JavaPlugin
{
    private KsiuCommandRouter _commandRouter;

    public final String getVersion()
    {
        return "1.0.0";
    }

    @Override
    public void onEnable()
    {
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

    public static TextComponent.Builder getPrefixTextBuilder()
    {
        return Component.text().content("[KsiuCore] ").color(NamedTextColor.AQUA);
    }

    public static TextComponent.Builder getErrorTextBuilder()
    {
        return Component.text().content("[KsiuCore:Error] ").color(NamedTextColor.RED);
    }
}
