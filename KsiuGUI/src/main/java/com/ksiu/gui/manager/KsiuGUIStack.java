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

    private static final Map<UUID, Stack<GUITrace>> _userStacks = new HashMap<>();

    public static void push(Player player, IGUI gui)
    {
        UUID uuid = player.getUniqueId();
        GUITrace trace = new GUITrace(gui);
        push(player, trace);
    }

    public static void push(Player player, IInventoryGUI gui)
    {
        UUID uuid = player.getUniqueId();
        InventoryGUITrace.addNavigationFlag(uuid);
        GUITrace trace = new InventoryGUITrace(gui);
        push(player, trace);
    }

    private static void push(Player player, GUITrace trace)
    {
        UUID uuid = player.getUniqueId();
        Stack<GUITrace> stack = _userStacks.computeIfAbsent(uuid, id -> new Stack<>());
        if (!stack.empty())
            stack.peek().close(player);

        Bukkit.getScheduler().runTask(KsiuGUI.getInstance(), () ->
        {
            stack.push(trace);
            trace.open(player);
        });
    }

    public static void pop(Player player)
    {
        UUID uuid = player.getUniqueId();
        Stack<GUITrace> stack = _userStacks.computeIfAbsent(uuid, id -> new Stack<>());
        if (stack.isEmpty())
            return;

        GUITrace gui = stack.peek();
        if (gui instanceof IInventoryGUI)
            player.closeInventory(); // 현재 창을 닫음 -> 클라이언트가 닫히면서 onClose 이벤트 발생
        else
        {
            gui.close(player);
            Bukkit.getScheduler().runTask(KsiuGUI.getInstance(), () ->
            {
                KsiuGUIStack.openPrev(player);
            });
        }
    }

    static GUITrace peek(Player player)
    {
        UUID uuid = player.getUniqueId();
        Stack<GUITrace> stack = _userStacks.get(uuid);
        if (stack == null || stack.empty())
            return null;

        return stack.peek();
    }

    public static void clear(Player player)
    {
        UUID uuid = player.getUniqueId();
        _userStacks.remove(uuid);
        InventoryGUITrace.removeNavigationFlag(uuid);
    }

    // onClose 이벤트용
    static void openPrev(Player player)
    {
        UUID uuid = player.getUniqueId();
        Stack<GUITrace> stack = _userStacks.get(uuid);
        if (stack == null || stack.isEmpty())
            return;

        stack.pop();
        if (!stack.empty())
        {
            stack.peek().open(player);
        }
        else
        {
            _userStacks.remove(uuid);
        }
    }

    static class GUITrace
    {
        private final IGUI _target;

        public GUITrace(IGUI target)
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

    static final class InventoryGUITrace extends GUITrace
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

        public InventoryGUITrace(IInventoryGUI target)
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
            Bukkit.getScheduler().runTask(KsiuGUI.getInstance(), () ->
            {
                Player player = (Player) event.getPlayer();
                UUID uuid = player.getUniqueId();
                if (_navigatingPlayers.contains(uuid))
                {
                    _navigatingPlayers.remove(uuid);
                    return;
                }
                KsiuGUIStack.openPrev(player);
            });
        }
    }

}
