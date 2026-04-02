package com.ksiu.gui.interfaces;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface IIntegerEvent
{
    void execute(Player player, int value);
}
