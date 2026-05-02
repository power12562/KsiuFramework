package com.ksiu.better.mm;

import com.ksiu.better.BetterNotice;
import com.ksiu.better.mm.mechanic.MobSpawnNoticeMechanic;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class MMNotice implements Listener
{
    private static MMNotice instance;

    public static MMNotice inst()
    {
        return instance;
    }

    public static void initialize(BetterNotice plugin)
    {
        instance = new MMNotice(plugin);
    }

    private final Logger _logger;

    private MMNotice(BetterNotice plugin)
    {
        _logger = plugin.getLogger();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event)
    {
        if (event.getMechanicName().equalsIgnoreCase("MobSpawnNotice"))
        {
            event.register(new MobSpawnNoticeMechanic(event.getConfig()));
            _logger.info("Registered Mechanic: MobSpawnNoticeMechanic");
        }
    }

}
