package com.ksiu.gui;

import com.ksiu.core.KsiuCore;
import com.ksiu.core.builders.ItemBuilder;
import com.ksiu.core.commands.base.CommandBase;
import com.ksiu.core.commands.base.OpCommandBase;
import com.ksiu.core.commands.container.KsiuCommandList;
import com.ksiu.gui.interfaces.IGUI;
import com.ksiu.gui.interfaces.IInventoryGUI;
import com.ksiu.gui.manager.KsiuGUIStack;
import com.ksiu.gui.virtualInventory.VirtualInventoryGUIBase;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class KsiuGUI extends JavaPlugin
{
    private static KsiuGUI instance;

    public static KsiuGUI getInstance()
    {
        return instance;
    }

    public static String getVersion()
    {
        return "1.0.0";
    }

    private final KsiuCommandList _commandList = new KsiuCommandList("gui");

    private static class GUIListener implements Listener
    {
        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent event)
        {
            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof IGUI))
                return;

            if (holder instanceof IInventoryGUI gui)
                gui.onOpen(event);
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event)
        {
            InventoryHolder holder = event.getInventory().getHolder();
            if (!(holder instanceof IGUI))
                return;

            if (holder instanceof IInventoryGUI gui)
                gui.onClose(event);
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event)
        {
            if (!(event.getInventory().getHolder() instanceof IGUI))
                return;

            if (event.getInventory().getHolder() instanceof IInventoryGUI gui)
            {
                event.setCancelled(true);
                if (event.getRawSlot() < event.getInventory().getSize())
                    gui.onClick(event);
            }
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event)
        {
            KsiuGUIStack.clear(event.getPlayer());
        }

    }

    @Override
    public void onEnable()
    {
        KsiuCore _ksiuCore = KsiuCore.getInstance();
        if (_ksiuCore == null)
        {
            getLogger().severe("KsiuCore를 찾을 수 없습니다. GUI 플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        instance = this;
        getLogger().info("KsiuGUI 플러그인 활성화.");
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        _ksiuCore.getCommandRouter().registerCommandBundle(_commandList.getModuleName(), _commandList);
        _commandList.put(new VersionCommand());
        _commandList.put(new TestCommand());
    }

    @Override
    public void onDisable()
    {
        getLogger().info("KsiuGUI 플러그인 비활성화.");
    }

    private static final class VersionCommand extends CommandBase
    {
        public VersionCommand()
        {
            super("version", "플러그인 버전입니다.");
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args)
        {
            sender.sendMessage(KsiuCore.getPrefixTextBuilder()
                    .append(Component.text(getVersion()))
                    .build());
            return true;
        }
    }

    private static final class TestGUI extends VirtualInventoryGUIBase
    {
        public TestGUI(String name, ESize eSize)
        {
            super(name, eSize, Component.text(name));
            Material[] materials = Material.values();
            for (int i = 0; i < eSize.getValue(); i++)
            {
                ItemStack item = new ItemBuilder(materials[i])
                        .setName(String.valueOf(i))
                        .setLore(Collections.emptyList())
                        .build();
                final int slotIndex = i;
                setItem(i, item, event ->
                {
                    event.getWhoClicked().sendMessage(KsiuCore.getPrefixTextBuilder()
                            .append(Component.text(MessageFormat.format("Click: {0}", String.valueOf(slotIndex))))
                            .build());
                });
            }
        }

        @Override
        public void onOpen(InventoryOpenEvent event)
        {
            event.getPlayer().sendMessage(KsiuCore.getPrefixTextBuilder()
                    .append("Test GUI open.")
                    .build()
            );
        }

        @Override
        public void onClose(InventoryCloseEvent event)
        {
            event.getPlayer().sendMessage(KsiuCore.getPrefixTextBuilder()
                    .append("Test GUI close.")
                    .build()
            );
        }

    }

    private static final class TestCommand extends OpCommandBase
    {
        public TestCommand()
        {
            super("test", "테스트용 GUI를 생성합니다.");
        }

        @Override
        public boolean onOpCommand(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
                return false;

            if (args.length < 2)
            {
                player.sendMessage(Component.text("사용법: /gui test [이름] [9, 18, 27, 36, 45, 54]"));
                return true;
            }

            String guiName = args[0];
            try
            {
                int sizeValue = Integer.parseInt(args[1]);
                VirtualInventoryGUIBase.ESize size = VirtualInventoryGUIBase.ESize.fromInt(sizeValue);

                if (size == null)
                {
                    player.sendMessage(Component.text("지원하지 않는 사이즈입니다. (9의 배수, 최대 54)"));
                    return true;
                }

                TestGUI gui = new TestGUI(guiName, size);
                gui.open(player);
            }
            catch (NumberFormatException e)
            {
                player.sendMessage(Component.text("사이즈는 반드시 숫자로 입력해야 합니다."));
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (args.length == 1)
                return List.of("<GUI이름>");

            if (args.length == 2)
            {
                List<String> suggestions = new ArrayList<>();
                for (VirtualInventoryGUIBase.ESize size : VirtualInventoryGUIBase.ESize.values())
                {
                    suggestions.add(String.valueOf(size.getValue()));
                }
                return suggestions;
            }
            return List.of();
        }
    }
}
