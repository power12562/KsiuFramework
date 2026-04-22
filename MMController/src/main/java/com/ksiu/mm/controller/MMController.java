package com.ksiu.mm.controller;

import com.ksiu.mm.controller.mechanic.MoveToTargetMechanic;
import com.ksiu.mm.controller.mechanic.TestMechanic;import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MMController extends JavaPlugin implements Listener
{
    private static Logger logger;
    public static Logger getPluginLogger()
    {
        return logger;
    }

    @Override
    public void onEnable()
    {
        logger = getLogger();
        Bukkit.getPluginManager().registerEvents(this, this);
        logger.info(" Enable MMController");
    }

    @Override
    public void onDisable()
    {
        logger.info(" Disable MMController");
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event)
    {
        if (event.getMechanicName().equalsIgnoreCase("test_mechanic"))
        {
            event.register(new TestMechanic(event.getConfig()));
            logger.info("Registered Mechanic: TestMechanic");
            return;
        }

        if (event.getMechanicName().equalsIgnoreCase("moveToTarget"))
        {
            event.register(new MoveToTargetMechanic(event.getConfig()));
            logger.info("Registered Mechanic: MoveToTargetMechanic");
            return;
        }
    }
}
