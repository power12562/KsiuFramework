package com.ksiu.better;

import com.ksiu.better.placeholders.NoticePlaceholder;
import kr.toxicity.hud.api.BetterHud;
import kr.toxicity.hud.api.BetterHudAPI;
import kr.toxicity.hud.api.manager.PlaceholderManager;
import kr.toxicity.hud.api.placeholder.PlaceholderContainer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterNotice extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        BetterHud betterHud = BetterHudAPI.inst();
        PlaceholderManager placeholders = betterHud.getPlaceholderManager();
        PlaceholderContainer<String> stringContainer = placeholders.getStringContainer();
        NoticePlaceholder noticePlaceholder = new NoticePlaceholder("godlight_notice_popup");

        stringContainer.addPlaceholder("better_notice", noticePlaceholder);
        registerCommand(this, "공지", noticePlaceholder);
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic


    }

    public static void registerCommand(JavaPlugin plugin, String name, CommandExecutor executor)
    {
        PluginCommand cmd = plugin.getCommand(name);
        if (cmd != null)
        {
            cmd.setExecutor(executor);
        }
    }
}
