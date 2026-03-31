package com.ksiu.gui.manager;

import com.ksiu.gui.interfaces.IGUI;
import com.ksiu.gui.interfaces.IInventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public class GUIListener implements Listener
{
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof IGUI))
            return;

        if (holder instanceof IInventoryGUI gui)
        {
            Player player = (Player) event.getPlayer();
            KsiuGUIStack.Tracer stackPeek = KsiuGUIStack.peek(player);
            if (stackPeek instanceof KsiuGUIStack.InventoryTracer trace)
                trace.onOpen(event);
            else
                gui.onOpen(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof IGUI))
            return;


        if (holder instanceof IInventoryGUI gui)
        {
            Player player = (Player) event.getPlayer();
            KsiuGUIStack.Tracer stackPeek = KsiuGUIStack.peek(player);
            if (stackPeek instanceof KsiuGUIStack.InventoryTracer trace)
                trace.onClose(event);
            else
                gui.onClose(event);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (!(event.getInventory().getHolder() instanceof IGUI))
            return;

        if (event.getInventory().getHolder() instanceof IInventoryGUI gui)
        {
            event.setCancelled(true);
            if (event.getRawSlot() < event.getInventory().getSize())
            {
                gui.onClick(event);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        KsiuGUIStack.clear(event.getPlayer());
    }

}