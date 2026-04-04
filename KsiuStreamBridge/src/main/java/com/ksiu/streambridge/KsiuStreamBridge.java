package com.ksiu.streambridge;

import com.ksiu.commons.streamconnector.authorizer.ChzzkAuthorizer;
import com.ksiu.commons.streamconnector.token.ChzzkToken;
import com.ksiu.core.KsiuCore;
import com.ksiu.core.commands.base.CommandBase;
import com.ksiu.core.commands.base.OpCommandBase;
import com.ksiu.core.commands.container.KsiuCommandList;
import com.ksiu.gui.KsiuGUI;
import com.ksiu.gui.manager.KsiuGUIStack;
import com.ksiu.streambridge.gui.APIConnectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class KsiuStreamBridge extends JavaPlugin implements Listener
{
    private static KsiuStreamBridge instance;
    private final KsiuCommandList _commandList = new KsiuCommandList("streamBridge");
    private boolean _isValidChzzk;
    private ChzzkAuthorizer _chzzkAuthorizer;

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
        getServer().getPluginManager().registerEvents(this, this);
        _ksiuCore.getCommandRouter().registerCommandBundle("streamBridge", _commandList);
        _commandList.put(new VersionCommand());
        _commandList.put(new ConnectCommand());
        _commandList.put(new ReadPropertiesCommand());
    }

    private final boolean isValidChzzk()
    {
        return _isValidChzzk;
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
        String chzzkClientId = properties.getProperty("client_id");
        if (chzzkClientId == null || chzzkClientId.startsWith("INPUT_YOUR"))
            return false;
        String chzzkClientSecret = properties.getProperty("client_secret");
        if (chzzkClientSecret == null || chzzkClientSecret.startsWith("INPUT_YOUR"))
            return false;
        String portStr = properties.getProperty("port");
        if (portStr == null || portStr.startsWith("INPUT_YOUR"))
            return false;
        int chzzkPort = 50500;
        try
        {
            chzzkPort = Integer.parseInt(portStr.trim());
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        _chzzkAuthorizer = new ChzzkAuthorizer(chzzkClientId, chzzkClientSecret, chzzkPort);

        //TODO: Session 관리 추가 필요

        return true;
    }

    private final Map<UUID, ChzzkToken> _uuidByChzzkToken = new HashMap<>();

    public void authorizerChzzk(Player player)
    {
        if (!isValidChzzk())
        {
            player.sendMessage(KsiuCore.getErrorTextBuilder().append("치지직 API가 비활성화 상태입니다. 관리자에게 문의하세요.").build());
            return;
        }

        UUID uuid = player.getUniqueId();
        if (_uuidByChzzkToken.containsKey(uuid))
        {
            player.sendMessage(KsiuCore.getPrefixTextBuilder().append("API가 이미 연동되었습니다.").build());
            return;
        }

        player.sendMessage(KsiuCore.getPrefixTextBuilder().append("치지직 인증을 시작합니다...").build());
        _chzzkAuthorizer.requestToken().thenAccept(newToken ->
        {
            Bukkit.getScheduler().runTask(this, () ->
            {
                player.sendMessage(KsiuCore.getPrefixTextBuilder().append("치지직 인증이 완료되었습니다.").build());
                _uuidByChzzkToken.put(uuid, newToken);
            });
        }).exceptionally(ex ->
        {
            Bukkit.getScheduler().runTask(this, () ->
            {
                player.sendMessage(KsiuCore.getErrorTextBuilder().append("API 인증에 실패했습니다.").build());
                player.sendMessage(KsiuCore.getErrorTextBuilder().append(ex.toString()).build());
            });
            return null;
        }).orTimeout(5, TimeUnit.MINUTES);
    }

    public boolean hasChzzkToken(Player player)
    {
        UUID uuid = player.getUniqueId();
        return _uuidByChzzkToken.containsKey(uuid);
    }

    public void removeChzzkToken(Player player)
    {
        UUID uuid = player.getUniqueId();
        ChzzkToken token = _uuidByChzzkToken.remove(uuid);
        if (token != null)
        {
            token.revokeAccessToken();
            token.revokeRefreshToken();
        }
    }

    public void refreshChzzkToken(Player player)
    {
        UUID uuid = player.getUniqueId();
        ChzzkToken token = _uuidByChzzkToken.get(uuid);
        if (token == null)
            return;

        player.sendMessage(KsiuCore.getPrefixTextBuilder().append("치지직 재인증을 시작합니다...").build());
        _chzzkAuthorizer.refreshToken(token.getRefreshToken()).thenAccept(newToken ->
        {
            Bukkit.getScheduler().runTask(this, () ->
            {
                player.sendMessage(KsiuCore.getPrefixTextBuilder().append("치지직 재인증이 완료되었습니다.").build());
                _uuidByChzzkToken.put(uuid, newToken);
            });
        }).exceptionally(ex ->
        {
            Bukkit.getScheduler().runTask(this, () ->
            {
                player.sendMessage(KsiuCore.getErrorTextBuilder().append("API 재인증에 실패했습니다.").build());
                player.sendMessage(KsiuCore.getErrorTextBuilder().append(ex.toString()).build());
            });
            return null;
        }).orTimeout(5, TimeUnit.MINUTES);

    }

    private void clearChzzkToken()
    {
        _uuidByChzzkToken.forEach((uuid, token) ->
        {
            token.revokeAccessToken();
            token.revokeRefreshToken();
        });
        _uuidByChzzkToken.clear();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        removeChzzkToken(event.getPlayer());
    }

    @Override
    public void onDisable()
    {
        clearChzzkToken();
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

            Player target;
            if (args.length == 0)
            {
                target = player;
            }
            else
            {
                target = Bukkit.getPlayer(args[0]);
                if (target == null)
                {
                    sender.sendMessage(KsiuCore.getErrorTextBuilder().append("해당 플레이어를 찾을 수 없습니다: " + args[0]).build());
                    return true;
                }
            }

            KsiuGUIStack.push(target, new APIConnectorGUI());
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (args.length == 1)
            {
                List<String> playerNames = new ArrayList<>();
                String currentInput = args[0].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (player.getName().toLowerCase().startsWith(currentInput))
                    {
                        playerNames.add(player.getName());
                    }
                }
                return playerNames;
            }
            return Collections.emptyList();
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

}
