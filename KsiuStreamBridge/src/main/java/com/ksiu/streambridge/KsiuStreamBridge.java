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

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public final class KsiuStreamBridge extends JavaPlugin
{
    private static KsiuStreamBridge instance;
    private final KsiuCommandList _commandList = new KsiuCommandList("streamBridge");
    private boolean _isValidChzzk;
    private String _chzzkClientId;
    private String _chzzkClientSecret;
    private int _chzzkPort;

    public static KsiuStreamBridge getInstance()
    {
        return instance;
    }

    public static final String VERSION = "1.0.0";

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
        _isValidChzzk = readChzzkProperties();
        _ksiuCore.getCommandRouter().registerCommandBundle("streamBridge", _commandList);
        _commandList.put(new VersionCommand());
        _commandList.put(new ConnectCommand());
        _commandList.put(new StreamConnectorCommand());
    }

    public final boolean isValidChzzk()
    {
        return _isValidChzzk;
    }

    public final String getChzzkClientId()
    {
        return _chzzkClientId;
    }

    public final String getChzzkClientSecret()
    {
        return _chzzkClientSecret;
    }

    private boolean readChzzkProperties()
    {
        Properties properties = KsiuCore.readProperties("chzzkAPI");
        if (properties.isEmpty())
        {
            properties.setProperty("client_id", "INPUT_YOUR_ID_HERE");
            properties.setProperty("client_secret", "INPUT_YOUR_SECRET_HERE");
            properties.setProperty("port", "INPUT_YOUR_PORT_HERE");
            try
            {
                KsiuCore.writeProperties("chzzkAPI", properties);
            }
            catch (IOException ex)
            {
                getLogger().warning("[Ksiu:StreamBridge]" + ex.toString());
            }
            getLogger().warning("Ksiu/chzzkAPI.properties 파일을 작성해주세요.");
            return false;
        }
        _chzzkClientId = properties.getProperty("client_id");
        if (_chzzkClientId == null || _chzzkClientId.startsWith("INPUT_YOUR"))
            return false;
        _chzzkClientSecret = properties.getProperty("client_secret");
        if (_chzzkClientSecret == null || _chzzkClientSecret.startsWith("INPUT_YOUR"))
            return false;
        String portStr = properties.getProperty("port");
        if (portStr == null || portStr.startsWith("INPUT_YOUR"))
            return false;
        try
        {
            _chzzkPort = Integer.parseInt(portStr.trim());
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        return true;
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

    private static final class ReadPropertiesCommand extends OpCommandBase
    {
        public ReadPropertiesCommand()
        {
            super("readproperties", "프로퍼티 파일을 다시 읽습니다.");
        }

        @Override
        public boolean onOpCommand(CommandSender sender, String[] args)
        {
            KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
            if (sb == null)
                return true;
            sb.readChzzkProperties();
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
