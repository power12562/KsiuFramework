package com.ksiu.gui.interfaces;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface ICancelEvent
{
    void execute(Player player);
}
