package com.ksiu.gui.dialog;

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
import java.util.function.BiConsumer;

public class DialogInputString extends DialogGUIBase
{
    public DialogInputString(String name, String label, BiConsumer<Player, String> outEvent)
    {
        super(name);
        _label = label;
        _outEvent = outEvent;
    }

    private final String _label;
    private final BiConsumer<Player, String> _outEvent;

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

                                                        _outEvent.accept(player, input);
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
