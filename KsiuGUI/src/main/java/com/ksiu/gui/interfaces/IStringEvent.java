package com.ksiu.gui.interfaces;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface IStringEvent
{
    void execute(Player player, String value);
}