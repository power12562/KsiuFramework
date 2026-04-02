package com.ksiu.gui.dialog;

import com.ksiu.gui.interfaces.ICancelEvent;
import com.ksiu.gui.interfaces.IStringEvent;
import com.ksiu.gui.manager.KsiuGUIStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DialogInputString extends DialogGUIBase
{
    public DialogInputString(String name, String label, IStringEvent outEvent)
    {
        super(name);
        _label = label;
        _outEvent = outEvent;
        _cancelEvent = null;
    }

    public DialogInputString(String name, String label, IStringEvent outEvent, ICancelEvent cancelEvent)
    {
        super(name);
        _label = label;
        _outEvent = outEvent;
        _cancelEvent = cancelEvent;
    }

    private final String _label;
    private final IStringEvent _outEvent;
    private final ICancelEvent _cancelEvent;

    @Override
    protected @NotNull Dialog onDialogBuild()
    {
        Dialog dialog = Dialog.create(builder ->
        {
            builder.empty()
                    .base(DialogBase.builder(Component.text(getName()))
                            .inputs(List.of(
                                    DialogInput.text(_label, Component.text(_label)).build()
                            ))
                            .canCloseWithEscape(false)
                            .build())
                    .type(DialogType.confirmation(
                                    ActionButton.builder(Component.text("확인", TextColor.color(0xAEFFC1)))
                                            .action(DialogAction.customClick(
                                                    (view, audience) ->
                                                    {
                                                        if (!(audience instanceof Player player))
                                                            return;

                                                        String input = view.getText(_label);
                                                        if (input == null)
                                                            return;

                                                        _outEvent.execute(player, input);
                                                        KsiuGUIStack.popOrClose(player, this);
                                                    },
                                                    ClickCallback.Options.builder().build()
                                            ))
                                            .build(),
                                    ActionButton.builder(Component.text("취소", TextColor.color(0xFFA0B1)))
                                            .action(DialogAction.customClick(
                                                    (view, audience) ->
                                                    {
                                                        if (!(audience instanceof Player player))
                                                            return;

                                                        if (_cancelEvent != null)
                                                            _cancelEvent.execute(player);

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
