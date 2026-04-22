package com.ksiu.mm.controller;

import com.ksiu.mm.controller.mechanic.TestSkill;import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class controller extends JavaPlugin implements Listener
{
    private Logger log;

    @Override
    public void onEnable()
    {
        log = getLogger();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable()
    {

    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event)
    {
        log.info("MythicMechanicLoadEvent called for mechanic " + event.getMechanicName());
        if(event.getMechanicName().equalsIgnoreCase("test_skill"))
        {
            event.register(new TestSkill(event.getConfig()));
            log.info("-- Registered test mechanic!");
        }
    }
}
