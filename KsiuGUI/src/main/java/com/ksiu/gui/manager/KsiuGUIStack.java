package com.ksiu.gui.manager;

import com.ksiu.gui.KsiuGUI;
import com.ksiu.gui.interfaces.IGUI;
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

    private static final Map<UUID, Stack<IGUI>> _userStacks = new HashMap<>();

    public static void push(Player player, IGUI gui)
    {
        UUID uuid = player.getUniqueId();
        Stack<IGUI> stack = _userStacks.computeIfAbsent(uuid, id -> new Stack<>());

        stack.push(new GUITrace(gui));
        GUITrace.addNavigationFlag(uuid);
        gui.open(player);
    }

    public static void pop(Player player)
    {
        UUID uuid = player.getUniqueId();
        Stack<IGUI> stack = _userStacks.computeIfAbsent(uuid, id -> new Stack<>());
        if (stack.isEmpty())
            return;

        // 현재 창을 닫음 -> 클라이언트가 닫히면서 onClose 이벤트 발생
        player.closeInventory();
    }

    public static void clear(Player player)
    {
        UUID uuid = player.getUniqueId();
        _userStacks.remove(uuid);
        GUITrace.removeNavigationFlag(uuid);
    }

    // onClose 이벤트용
    static void openPrev(Player player)
    {
        UUID uuid = player.getUniqueId();
        Stack<IGUI> stack = _userStacks.get(uuid);
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

    private static final class GUITrace implements IGUI
    {
        private final IGUI _target;
        private static final Set<UUID> _navigatingPlayers = new HashSet<>();

        public static void addNavigationFlag(UUID uuid)
        {
            _navigatingPlayers.add(uuid);
        }

        public static void removeNavigationFlag(UUID uuid)
        {
            _navigatingPlayers.remove(uuid);
        }

        public GUITrace(IGUI target)
        {
            _target = target;
        }

        @Override
        public void open(@NotNull Player player)
        {
            _target.open(player);
        }

        @Override
        public void close(@NotNull Player player)
        {
            _target.close(player);
        }

        @Override
        public void onOpen(InventoryOpenEvent event)
        {
            _target.onOpen(event);
        }

        @Override
        public void onClose(InventoryCloseEvent event)
        {
            _target.onClose(event);
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

        @Override
        public @NotNull String getName()
        {
            return _target.getName();
        }
    }

}
