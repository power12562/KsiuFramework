package com.ksiu.gui.interfaces;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface IConfirmEvent
{
    void execute(Player player);
}
