package com.ksiu.gui.interfaces;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IGUI
{
    void open(@NotNull Player player);

    void close(@NotNull Player player);

    @NotNull String getName();
}
