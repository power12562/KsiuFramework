package com.ksiu.streambridge.gui;

import com.ksiu.gui.dialog.DialogGUIBase;
import com.ksiu.gui.interfaces.IConfirmEvent;
import com.ksiu.gui.manager.KsiuGUIStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DialogAPISessionControl extends DialogGUIBase
{
    private final IConfirmEvent _extendEvent;
    private final IConfirmEvent _clearEvent;

    protected DialogAPISessionControl(IConfirmEvent extendEvent, IConfirmEvent clearEvent)
    {
        super("API 세션 관리");
        _extendEvent = extendEvent;
        _clearEvent = clearEvent;
    }

    @Override
    protected @NotNull Dialog onDialogBuild()
    {
        Dialog dialog = Dialog.create(builder ->
        {
            builder.empty()
                    .base(DialogBase.builder(Component.text(getName()))
                            .canCloseWithEscape(true)
                            .build())
                    .type(DialogType.confirmation(
                                    ActionButton.builder(Component.text("API 기간 연장", TextColor.color(0xAEFFC1)))
                                            .action(DialogAction.customClick(
                                                    (view, audience) ->
                                                    {
                                                        if (!(audience instanceof Player player))
                                                            return;

                                                        _extendEvent.execute(player);
                                                        KsiuGUIStack.popOrClose(player, this);
                                                    },
                                                    ClickCallback.Options.builder().build()
                                            ))
                                            .build(),
                                    ActionButton.builder(Component.text("API 연동 해제", TextColor.color(0xFFA0B1)))
                                            .action(DialogAction.customClick(
                                                    (view, audience) ->
                                                    {
                                                        if (!(audience instanceof Player player))
                                                            return;

                                                        _clearEvent.execute(player);
                                                        KsiuGUIStack.popOrClose(player, this);
                                                    },
                                                    ClickCallback.Options.builder().build()
                                            ))
                                            .build()

                            )
                    );
        });
        return dialog;
    }

}
