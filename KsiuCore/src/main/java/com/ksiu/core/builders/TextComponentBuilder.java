package com.ksiu.core.builders;

import net.kyori.adventure.builder.AbstractBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextComponentBuilder
{
    @NotNull
    public static TextComponentBuilder newBuilder()
    {
        return new TextComponentBuilder();
    }

    private final TextComponent.Builder _builder;

    private TextComponentBuilder()
    {
        _builder = Component.text();
    }

    @NotNull
    public TextComponent.Builder asBuilder()
    {
        return _builder;
    }

    @NotNull
    public String content()
    {
        return _builder.content();
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder colorIfAbsent(@Nullable TextColor color)
    {
        _builder.colorIfAbsent(color);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public TextComponentBuilder decoration(@NotNull TextDecoration decoration, TextDecoration.@NotNull State state)
    {
        _builder.decoration(decoration, state);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public TextComponentBuilder mergeStyle(@NotNull Component that, Style.@NotNull Merge @NotNull ... merges)
    {
        _builder.mergeStyle(that, merges);
        return this;
    }


    @NotNull
    @Contract("_, _ -> this")
    public TextComponentBuilder decorationIfAbsent(@NotNull TextDecoration decoration, TextDecoration.@NotNull State state)
    {
        _builder.decorationIfAbsent(decoration, state);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public TextComponentBuilder mergeStyle(@NotNull Component that, @NotNull Set<Style.Merge> merges)
    {
        _builder.mergeStyle(that, merges);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder append(@NotNull ComponentLike @NotNull ... components)
    {
        _builder.append(components);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public TextComponentBuilder decorations(@NotNull Set<TextDecoration> decorations, boolean flag)
    {
        _builder.decorations(decorations, flag);
        return this;
    }

    public void componentBuilderApply(@NotNull ComponentBuilder<?, ?> component)
    {
        _builder.componentBuilderApply(component);
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder applyDeep(@NotNull Consumer<? super ComponentBuilder<?, ?>> action)
    {
        _builder.applyDeep(action);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder append(@NotNull ComponentBuilder<?, ?> builder)
    {
        _builder.append(builder);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder color(@Nullable TextColor color)
    {
        _builder.color(color);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder decorations(@NotNull Map<TextDecoration, TextDecoration.State> decorations)
    {
        _builder.decorations(decorations);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder mergeStyle(@NotNull Component that)
    {
        _builder.mergeStyle(that);
        return this;
    }

    @NotNull
    public Component asComponent()
    {
        return _builder.asComponent();
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder hoverEvent(@Nullable HoverEventSource<?> source)
    {
        _builder.hoverEvent(source);
        return this;
    }

    @NotNull
    public TextComponentBuilder appendNewline()
    {
        _builder.appendNewline();
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder decorate(@NotNull TextDecoration @NotNull ... decorations)
    {
        _builder.decorate(decorations);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder applicableApply(@NotNull ComponentBuilderApplicable applicable)
    {
        _builder.applicableApply(applicable);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder append(@NotNull Component component)
    {
        _builder.append(component);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder append(@NotNull String string)
    {
        _builder.append(Component.text(string));
        return this;
    }

    @NotNull
    public TextComponent build()
    {
        return _builder.build();
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder style(@NotNull Consumer<Style.Builder> consumer)
    {
        _builder.style(consumer);
        return this;
    }

    @NotNull
    public TextComponentBuilder appendSpace()
    {
        _builder.appendSpace();
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder insertion(@Nullable String insertion)
    {
        _builder.insertion(insertion);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder font(@Nullable Key font)
    {
        _builder.font(font);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder apply(@NotNull Consumer<? super ComponentBuilder<?, ?>> consumer)
    {
        _builder.apply(consumer);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder append(@NotNull ComponentLike component)
    {
        _builder.append(component);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder content(@NotNull String content)
    {
        _builder.content(content);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public TextComponentBuilder decoration(@NotNull TextDecoration decoration, boolean flag)
    {
        _builder.decoration(decoration, flag);
        return this;
    }

    public @NotNull List<Component> children()
    {
        return _builder.children();
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder append(@NotNull Iterable<? extends ComponentLike> components)
    {
        _builder.append(components);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder style(@NotNull Style style)
    {
        _builder.style(style);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder decorate(@NotNull TextDecoration decoration)
    {
        _builder.decorate(decoration);
        return this;
    }

    @NotNull
    @Contract("_ -> this")
    public TextComponentBuilder clickEvent(@Nullable ClickEvent event)
    {
        _builder.clickEvent(event);
        return this;
    }

    @NotNull
    public TextComponentBuilder resetStyle()
    {
        _builder.resetStyle();
        return this;
    }

    @NotNull
    @Contract(mutates = "param1")
    public static <R, B extends AbstractBuilder<R>> R configureAndBuild(@NonNull B builder, @Nullable Consumer<? super B> consumer)
    {
        return AbstractBuilder.configureAndBuild(builder, consumer);
    }

    public static @NotNull List<Component> asComponents(@NotNull List<? extends ComponentLike> likes)
    {
        return ComponentLike.asComponents(likes);
    }

    public static @NotNull List<Component> asComponents(@NotNull List<? extends ComponentLike> likes, @Nullable Predicate<? super Component> filter)
    {
        return ComponentLike.asComponents(likes, filter);
    }

    @Nullable
    public static Component unbox(@Nullable ComponentLike like)
    {
        return ComponentLike.unbox(like);
    }

}
