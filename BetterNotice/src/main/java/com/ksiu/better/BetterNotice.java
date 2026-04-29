package com.ksiu.better;

import kr.toxicity.hud.api.BetterHud;
import kr.toxicity.hud.api.BetterHudAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterNotice extends JavaPlugin
{
    private BetterHud _betterHud;

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        _betterHud = BetterHudAPI.inst();
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }
}
