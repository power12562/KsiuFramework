package com.ksiu.gui.dialog;

import com.ksiu.gui.interfaces.IGUI;
import io.papermc.paper.dialog.Dialog;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class DialogGUIBase implements IGUI
{
    private final String _name;

    protected DialogGUIBase(String name)
    {
        _name = name;
    }

    @NotNull
    protected abstract Dialog onDialogBuild();

    @Override
    public final void open(@NotNull Player player)
    {
        Dialog _dialog = onDialogBuild();
        player.showDialog(_dialog);
    }

    @Override
    public final void close(@NotNull Player player)
    {
        // 닫는건 다이얼로그 버튼이 직접 처리함.
        return;
    }

    @Override
    @NotNull
    public final String getName()
    {
        return _name;
    }
}
