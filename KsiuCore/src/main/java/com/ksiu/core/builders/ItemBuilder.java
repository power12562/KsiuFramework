package com.ksiu.core.builders;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder
{
    private final ItemStack _itemStack;
    private final ItemMeta _meta;

    public static ItemBuilder newBuilder(Material material)
    {
        return new ItemBuilder(material);
    }

    public ItemBuilder(Material material)
    {
        _itemStack = new ItemStack(material);
        _meta = _itemStack.getItemMeta();
    }

    public ItemBuilder setName(String name)
    {
        return setName(Component.text(name));
    }

    public ItemBuilder setName(Component name)
    {
        if (_meta != null)
            _meta.displayName(name);
        return this;
    }

    public ItemBuilder setLore(List<Component> lore)
    {
        if (_meta != null)
            _meta.lore(lore);
        return this;
    }

    public ItemBuilder addLore(Component line)
    {
        if (_meta != null)
        {
            List<Component> lore = _meta.lore();
            if (lore == null)
                lore = new ArrayList<>();
            lore.add(line);
            _meta.lore(lore);
        }
        return this;
    }

    public ItemStack build()
    {
        _itemStack.setItemMeta(_meta);
        return _itemStack;
    }
}