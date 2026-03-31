package com.ksiu.gui.interfaces;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public interface IInventoryGUI extends IGUI
{
    default void onOpen(InventoryOpenEvent event)
    {
    }

    default void onClose(InventoryCloseEvent event)
    {
    }

    default void onClick(InventoryClickEvent event)
    {
    }
}
