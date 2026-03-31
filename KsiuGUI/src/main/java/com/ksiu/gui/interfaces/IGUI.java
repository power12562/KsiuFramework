package com.ksiu.gui.interfaces;

import org.bukkit.entity.Player;

public interface IGUI
{
    void open(Player player);
    void close(Player player);
    String getName();
}
