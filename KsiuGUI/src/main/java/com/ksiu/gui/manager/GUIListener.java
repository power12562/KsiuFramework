package com.ksiu.gui.manager;

import com.ksiu.gui.interfaces.IGUI;
import com.ksiu.gui.interfaces.IInventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public class GUIListener implements Listener
{
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof IInventoryGUI gui))
            return;

        Player player = (Player) event.getPlayer();
        KsiuGUIStack.Tracer stackPeek = KsiuGUIStack.peek(player);
        if (stackPeek != null)
        {
            // 스택 push됨
            IGUI peek = stackPeek.getGUI();
            if (peek == gui)
            {
                if (stackPeek instanceof KsiuGUIStack.InventoryTracer trace)
                    trace.onOpen(event);

                return;
            }
            KsiuGUIStack.clear(player);
        }
        // 스택 없이 생성됨.
        gui.onOpen(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof IInventoryGUI gui))
            return;

        Player player = (Player) event.getPlayer();
        KsiuGUIStack.Tracer stackPeek = KsiuGUIStack.peek(player);
        if (stackPeek != null)
        {
            // 스택 push됨
            IGUI peek = stackPeek.getGUI();
            if (peek == gui)
            {
                if (stackPeek instanceof KsiuGUIStack.InventoryTracer trace)
                    trace.onClose(event);

                return;
            }
            KsiuGUIStack.clear(player);
        }
        // 스택 없이 생성됨.
        gui.onClose(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (!(event.getInventory().getHolder() instanceof IInventoryGUI gui))
            return;

        if (event.getRawSlot() < event.getInventory().getSize())
        {
            event.setCancelled(true);
            gui.onClick(event);
        }
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event)
    {
        int inventorySize = event.getInventory().getSize();
        for (int slot : event.getRawSlots())
        {
            if (slot < inventorySize)
            {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        KsiuGUIStack.clear(event.getPlayer());
    }

}