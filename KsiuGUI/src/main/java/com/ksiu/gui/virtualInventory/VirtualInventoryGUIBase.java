package com.ksiu.gui.virtualInventory;

import com.ksiu.gui.interfaces.IInventoryGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class VirtualInventoryGUIBase implements IInventoryGUI, InventoryHolder, Listener
{
    private final Inventory _inventory;
    private final String _name;
    private final InventoryType _type;
    private final int _size;
    private final Map<Integer, Consumer<InventoryClickEvent>> _actions = new HashMap<>();

    public enum ESize
    {
        Size9(9),
        Size18(18),
        Size27(27),
        Size36(36),
        Size45(45),
        Size54(54);
        private final int _value;

        ESize(int value)
        {
            _value = value;
        }

        public final int getValue()
        {
            return _value;
        }

        public static ESize fromInt(int value)
        {
            for (ESize size : values())
            {
                if (size._value == value)
                    return size;
            }
            return null;
        }
    }

    public VirtualInventoryGUIBase(String name, ESize eSize, Component title)
    {
        int size = eSize.getValue();
        _inventory = Bukkit.createInventory(this, size, title);
        _name = name;
        _size = size;
        _type = InventoryType.CHEST;
    }

    public VirtualInventoryGUIBase(String name, InventoryType type, Component title)
    {
        _inventory = Bukkit.createInventory(this, type, title);
        _name = name;
        _size = 0;
        _type = type;
    }

    @Override
    public final void open(@NotNull Player player)
    {
        player.openInventory(_inventory);
    }

    @Override
    public final void close(@NotNull Player player)
    {
        player.closeInventory();
    }

    @Override
    public final @NotNull Inventory getInventory()
    {
        return _inventory;
    }

    @Override
    public final @NonNull @NotNull String getName()
    {
        return _name;
    }

    public final void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> action)
    {
        _inventory.setItem(slot, item);
        if (action != null)
            _actions.put(slot, action);
        else
            _actions.remove(slot);
    }

    public final void removeItem(int slot)
    {
        _inventory.setItem(slot, null);
        _actions.remove(slot);
    }

    public final void clear()
    {
        _inventory.clear();
        _actions.clear();
    }

    public final void onClick(InventoryClickEvent event)
    {
        int slot = event.getRawSlot();
        if (_actions.containsKey(slot))
        {
            _actions.get(slot).accept(event);
        }
    }
}
