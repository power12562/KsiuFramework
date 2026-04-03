package com.ksiu.streambridge;

import com.ksiu.commons.streamconnector.StreamConnector;
import com.ksiu.core.KsiuCore;
import com.ksiu.core.commands.base.CommandBase;
import com.ksiu.core.commands.base.OpCommandBase;
import com.ksiu.core.commands.container.KsiuCommandList;
import com.ksiu.gui.KsiuGUI;
import com.ksiu.gui.manager.KsiuGUIStack;
import com.ksiu.streambridge.gui.APIConnectorGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class KsiuStreamBridge extends JavaPlugin
{
    private static KsiuStreamBridge instance;

    public static KsiuStreamBridge getInstance()
    {
        return instance;
    }

    public static final String VERSION = "1.0.0";

    private final KsiuCommandList _commandList = new KsiuCommandList("streamBridge");
    ;

    @Override
    public void onEnable()
    {
        KsiuCore _ksiuCore = KsiuCore.getInstance();
        if (_ksiuCore == null)
        {
            getLogger().severe("KsiuCore를 찾을 수 없습니다. StreamBridge 플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        instance = this;
        _ksiuCore.getCommandRouter().registerCommandBundle("streamBridge", _commandList);
        _commandList.put(new VersionCommand());
        _commandList.put(new ConnectCommand());
        _commandList.put(new StreamConnectorCommand());
    }

    @Override
    public void onDisable()
    {

    }

    private static final class VersionCommand extends CommandBase
    {
        public VersionCommand()
        {
            super("version", "플러그인 버전 정보를 확인합니다.");
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args)
        {
            sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(VERSION).build());
            return true;
        }
    }

    private static final class ConnectCommand extends OpCommandBase
    {
        public ConnectCommand()
        {
            super("connect", "api를 연동합니다. gui 플러그인이 존재해야합니다.");
        }

        @Override
        public boolean onOpCommand(CommandSender sender, String[] args)
        {
            KsiuGUI gui = KsiuGUI.getInstance();
            if (gui == null)
            {
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append("KsiuGUI 플러그인이 없습니다.").build());
                return true;
            }

            if (!(sender instanceof Player player))
                return true;

            KsiuGUIStack.push(player, new APIConnectorGUI());
            return true;
        }
    }

    private static final class StreamConnectorCommand extends OpCommandBase
    {
        public static String getModuleName()
        {
            return "streamconnector";
        }

        private final KsiuCommandList _ksiuCommandList;

        public StreamConnectorCommand()
        {
            super(getModuleName(), "api 연동 패키지 명령어 입니다.");
            _ksiuCommandList = new KsiuCommandList(getModuleName());
            _ksiuCommandList.put(new VersionCommand());
        }

        @Override
        public boolean onOpCommand(CommandSender sender, String[] args)
        {
            return _ksiuCommandList.onCommand(sender, args);
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String[] args)
        {
            return _ksiuCommandList.onTabComplete(sender, args);
        }

        public static final class VersionCommand extends OpCommandBase
        {
            public VersionCommand()
            {
                super("version", "패키지 버전 정보입니다.");
            }

            @Override
            public boolean onOpCommand(CommandSender sender, String[] args)
            {
                if (!(sender instanceof Player player))
                    return true;

                String version = StreamConnector.getVersion();
                player.sendMessage(KsiuCore.getPrefixTextBuilder().append(version).build());
                return true;
            }
        }
    }
}
