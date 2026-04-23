package com.ksiu.maze;

import com.ksiu.core.KsiuCore;
import org.bukkit.plugin.java.JavaPlugin;

public final class MazeGenerator extends JavaPlugin
{
    private KsiuCore _core;

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        _core = KsiuCore.getInstance();
        _core.getCommandRouter().registerCommandBundle("maze", MazeCommands.createCommandList(this));

    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }


}
