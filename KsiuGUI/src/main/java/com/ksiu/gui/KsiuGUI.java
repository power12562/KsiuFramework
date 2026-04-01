package com.ksiu.gui;

import com.ksiu.core.KsiuCore;
import com.ksiu.core.builders.ItemBuilder;
import com.ksiu.core.commands.base.CommandBase;
import com.ksiu.core.commands.base.OpCommandBase;
import com.ksiu.core.commands.container.KsiuCommandList;
import com.ksiu.gui.dialog.DialogInputString;
import com.ksiu.gui.manager.GUIListener;
import com.ksiu.gui.manager.KsiuGUIStack;
import com.ksiu.gui.virtualInventory.VirtualInventoryGUIBase;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
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
        _commandList.put(new DialogCommand());
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
        public static void newTestStack(Player player)
        {
            newTestStack(player, 0);
        }

        private static void newTestStack(Player player, int id)
        {
            if (id == 52)
                return;

            TestGUI stackGUI = new TestGUI(String.valueOf(id), ESize.Size54);
            for (int i = 0; i <= id; i++)
            {
                final int newID = i;
                stackGUI.setItem(newID, ItemBuilder.newBuilder(Material.DIAMOND_BLOCK)
                        .setName(String.valueOf(newID))
                        .build(), event ->
                {
                    newTestStack(player, id + 1);
                });
            }
            stackGUI.setItem(53, ItemBuilder.newBuilder(Material.REDSTONE_BLOCK).build(), event ->
            {
                KsiuGUIStack.pop(player);
            });
            KsiuGUIStack.push(player, stackGUI);
        }

        public TestGUI(String name, ESize eSize)
        {
            super(name, eSize, Component.text(name));
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

            if (args.length < 1)
            {
                player.sendMessage(Component.text("gui test [이름] [9, 18, 27, 36, 45, 54]"));
                return true;
            }

            if (args.length == 1)
            {
                if (args[0].equalsIgnoreCase("stack"))
                {
                    TestGUI.newTestStack(player);
                    player.sendMessage(Component.text("스택 테스트 GUI를 시작합니다."));
                    return true;
                }
            }

            if (args.length < 2)
            {
                player.sendMessage(Component.text("gui test [이름] [9, 18, 27, 36, 45, 54]"));
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
            {
                return List.of("stack", "<GUI이름>");
            }

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

    private static final class DialogTestGUI extends VirtualInventoryGUIBase
    {
        private final DialogInputString _inputDialog;

        public DialogTestGUI(String name)
        {
            super(name, ESize.Size9, Component.text(name));
            _inputDialog = new DialogInputString("문자열 입력 테스트 다이얼로그", "메시지", (player, message) ->
            {
                player.sendMessage(KsiuCore.getPrefixTextBuilder().append(message).build());
            });

            setItem(1, ItemBuilder.newBuilder(Material.DIAMOND_BLOCK)
                    .setName("문자열 입력해보기.")
                    .build(), event ->
            {
                KsiuGUIStack.push((Player) event.getWhoClicked(), _inputDialog);
            });

            setItem(7, ItemBuilder.newBuilder(Material.REDSTONE_BLOCK)
                    .setName("나가기.")
                    .build(), event ->
            {
                KsiuGUIStack.pop((Player) event.getWhoClicked());
            });
        }
    }

    private static final class DialogCommand extends OpCommandBase
    {
        public DialogCommand()
        {
            super("dialog", "다이얼로그 입력을 테스트합니다.");
        }

        @Override
        public boolean onOpCommand(CommandSender sender, String[] args)
        {
            if (!(sender instanceof Player player))
            {
                sender.sendMessage(Component.text("이 명령어는 플레이어만 사용할 수 있습니다."));
                return true;
            }
            DialogTestGUI testGui = new DialogTestGUI("테스트 메뉴");
            KsiuGUIStack.push(player, testGui);
            return true;
        }
    }

}
