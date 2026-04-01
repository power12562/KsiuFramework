package com.ksiu.gui.manager;

import com.ksiu.gui.KsiuGUI;
import com.ksiu.gui.interfaces.IGUI;
import com.ksiu.gui.interfaces.IInventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class KsiuGUIStack
{
    private KsiuGUIStack()
    {
    }

    private static final Map<UUID, Stack<Tracer>> _userStacks = new HashMap<>();

    public static void push(Player player, IGUI gui)
    {
        Tracer trace = new Tracer(gui);
        push(player, trace);
    }

    public static void push(Player player, IInventoryGUI gui)
    {
        UUID uuid = player.getUniqueId();
        InventoryTracer.addNavigationFlag(uuid);
        Tracer trace = new InventoryTracer(gui);
        push(player, trace);
    }

    private static void push(Player player, Tracer trace)
    {
        UUID uuid = player.getUniqueId();
        Stack<Tracer> stack = _userStacks.computeIfAbsent(uuid, id -> new Stack<>());
        if (!stack.empty())
            stack.peek().close(player);

        stack.push(trace);
        Bukkit.getScheduler().runTask(KsiuGUI.getInstance(), () ->
        {
            trace.open(player);
        });
    }

    public static void pop(Player player)
    {
        UUID uuid = player.getUniqueId();
        Stack<Tracer> stack = _userStacks.get(uuid);
        if (stack == null || stack.empty())
            return;

        Tracer trace = stack.peek();
        trace.close(player);
        if (trace instanceof InventoryTracer)
            return;

        Bukkit.getScheduler().runTask(KsiuGUI.getInstance(), () ->
        {
            KsiuGUIStack.openPrev(player);
        });
    }

    public static void popOrClose(@NotNull Player player, @NotNull IGUI owner)
    {
        Tracer peek = peek(player);
        if (peek != null)
        {
            if (peek.getGUI() == owner)
            {
                pop(player);
                return;
            }
            clear(player);
        }
        owner.close(player);
    }

    static Tracer peek(Player player)
    {
        UUID uuid = player.getUniqueId();
        Stack<Tracer> stack = _userStacks.get(uuid);
        if (stack == null || stack.empty())
            return null;

        return stack.peek();
    }

    public static void clear(Player player)
    {
        UUID uuid = player.getUniqueId();
        _userStacks.remove(uuid);
        InventoryTracer.removeNavigationFlag(uuid);
    }

    // onClose 이벤트용
    static void openPrev(Player player)
    {
        UUID uuid = player.getUniqueId();
        final Stack<Tracer> stack = _userStacks.get(uuid);
        if (stack == null || stack.isEmpty())
            return;

        stack.pop();
        if (!stack.empty())
        {
            Tracer peek = stack.peek();
            peek.open(player);
        }
        else
        {
            _userStacks.remove(uuid);
        }
    }

    static class Tracer
    {
        private final IGUI _target;

        @NotNull
        IGUI getGUI()
        {
            return _target;
        }

        public Tracer(@NotNull IGUI target)
        {
            _target = target;
        }

        public void open(@NotNull Player player)
        {
            _target.open(player);
        }

        public void close(@NotNull Player player)
        {
            _target.close(player);
        }

        public @NotNull String getName()
        {
            return _target.getName();
        }
    }

    static final class InventoryTracer extends Tracer
    {
        private final IInventoryGUI _inventory;
        private static final Set<UUID> _navigatingPlayers = new HashSet<>();

        public static void addNavigationFlag(UUID uuid)
        {
            _navigatingPlayers.add(uuid);
        }

        public static void removeNavigationFlag(UUID uuid)
        {
            _navigatingPlayers.remove(uuid);
        }

        public InventoryTracer(@NotNull IInventoryGUI target)
        {
            super(target);
            _inventory = target;
        }

        public void onOpen(InventoryOpenEvent event)
        {
            _inventory.onOpen(event);
        }

        public void onClose(InventoryCloseEvent event)
        {
            _inventory.onClose(event);
            Player player = (Player) event.getPlayer();
            UUID uuid = player.getUniqueId();
            if (_navigatingPlayers.contains(uuid))
            {
                _navigatingPlayers.remove(uuid);
                return;
            }

            Bukkit.getScheduler().runTask(KsiuGUI.getInstance(), () ->
            {
                KsiuGUIStack.openPrev(player);
            });
        }
    }

}
