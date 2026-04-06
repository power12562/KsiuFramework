package com.ksiu.streambridge;

import com.google.gson.Gson;
import com.ksiu.commons.shadow.org.json.JSONObject;
import com.ksiu.commons.streamconnector.chzzk.authorizer.ChzzkAuthorizer;
import com.ksiu.commons.streamconnector.chzzk.session.ChzzkSessionManager;
import com.ksiu.commons.streamconnector.chzzk.token.ChzzkToken;
import com.ksiu.core.KsiuCore;
import com.ksiu.core.commands.base.CommandBase;
import com.ksiu.core.commands.base.OpCommandBase;
import com.ksiu.core.commands.container.KsiuCommandList;
import com.ksiu.gui.KsiuGUI;
import com.ksiu.gui.manager.KsiuGUIStack;
import com.ksiu.streambridge.events.DonationCommandExecutor;
import com.ksiu.streambridge.gui.APIConnectorGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        readChzzkProperties();
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

    private void readChzzkProperties()
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
            _isValidChzzk = false;
            return;
        }
        String chzzkClientId = properties.getProperty("client_id");
        if (chzzkClientId == null || chzzkClientId.startsWith("INPUT_YOUR"))
        {
            _isValidChzzk = false;
            return;
        }
        String chzzkClientSecret = properties.getProperty("client_secret");
        if (chzzkClientSecret == null || chzzkClientSecret.startsWith("INPUT_YOUR"))
        {
            _isValidChzzk = false;
            return;
        }
        String portStr = properties.getProperty("port");
        if (portStr == null || portStr.startsWith("INPUT_YOUR"))
        {
            _isValidChzzk = false;
            return;
        }
        int chzzkPort = 50500;
        try
        {
            chzzkPort = Integer.parseInt(portStr.trim());
        }
        catch (NumberFormatException ex)
        {
            _isValidChzzk = false;
            return;
        }
        _chzzkAuthorizer = new ChzzkAuthorizer(chzzkClientId, chzzkClientSecret, chzzkPort);
        ChzzkSessionManager.initialize(chzzkClientId, chzzkClientSecret);
        clearChzzkToken();
        readChzzkJsonSettings();
        _isValidChzzk = true;
    }

    private final Map<String, JSONObject> _chzzkChannelIdByJsonSettings = new TreeMap<>();
    private static final String CHZZK_JSON_SETTINGS_FILE_NAME = "chzzkAPI.json";
    private static final String DEFAULT_SETTINGS_KEY = "default";
    private static final String CHAT_SETTINGS_KEY = "chat";
    private static final String DONATION_SETTINGS_KEY = "donation";
    private static final String SUBSCRIPTION_SETTINGS_KEY = "subscription";
    private final DonationCommandExecutor _chzzkDefaultDonationCommands = new DonationCommandExecutor();

    private void readChzzkJsonSettings()
    {
        Path path = Path.of(KsiuCore.getPropertiesPath(), CHZZK_JSON_SETTINGS_FILE_NAME);
        if (Files.exists(path))
        {
            try
            {
                String content = Files.readString(path);
                JSONObject jsonRoot = new JSONObject(content);
                _chzzkChannelIdByJsonSettings.clear();
                jsonRoot.keySet().forEach(key ->
                {
                    _chzzkChannelIdByJsonSettings.put(key, jsonRoot.getJSONObject(key));
                });

                JSONObject defaultSettings = _chzzkChannelIdByJsonSettings.get(DEFAULT_SETTINGS_KEY);
                JSONObject defaultDonationSettings = defaultSettings.getJSONObject(DONATION_SETTINGS_KEY);
                TreeMap<String, String> newCommands = new TreeMap<>();
                defaultDonationSettings.keySet().forEach(key ->
                {
                    newCommands.put(key, defaultDonationSettings.getString(key));
                });
                _chzzkDefaultDonationCommands.setExecuteCommands(newCommands);
            }
            catch (Exception ex)
            {
                getLogger().warning("잘못된" + CHZZK_JSON_SETTINGS_FILE_NAME + " 설정 파일입니다.");
            }
        }
        else
        {
            JSONObject defaultSettings = new JSONObject();
            JSONObject defaultDonationSettings = new JSONObject();

            String defaultCommand = "msg %player% %donator% 님이 %player% 님에게 천원 펀치!";
            defaultDonationSettings.put("1000", defaultCommand);
            defaultSettings.put(DONATION_SETTINGS_KEY, defaultDonationSettings);

            TreeMap<String, String> executeCommands = new TreeMap<>();
            executeCommands.put("1000", defaultCommand);
            _chzzkDefaultDonationCommands.setExecuteCommands(executeCommands);
            _chzzkChannelIdByJsonSettings.put(DEFAULT_SETTINGS_KEY, defaultSettings);
            writeChzzkJsonSettings();
        }
    }

    private void writeChzzkJsonSettings()
    {
        Path path = Path.of(KsiuCore.getPropertiesPath(), CHZZK_JSON_SETTINGS_FILE_NAME);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir))
        {
            try
            {
                Files.createDirectories(parentDir);
            }
            catch (IOException e)
            {
                getLogger().warning("치지직 API 설정 파일 저장 실패.");
                return;
            }
        }

        if (_chzzkChannelIdByJsonSettings.isEmpty())
        {
            getLogger().warning("치지직 API 설정이 존재하지 않습니다.");
            return;
        }

        try
        {
            JSONObject json = new JSONObject(_chzzkChannelIdByJsonSettings);
            String rawJson = json.toString();
            Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
            com.google.gson.JsonElement je = com.google.gson.JsonParser.parseString(rawJson);
            String prettyJson = gson.toJson(je);
            Files.writeString(path, prettyJson);
            getLogger().info("치지직 API 설정이 성공적으로 저장되었습니다.");
        }
        catch (Exception ex)
        {
            getLogger().warning("치지직 API 설정 파일 저장 실패.");
        }
    }

    private final Map<UUID, ChzzkToken> _uuidByChzzkToken = new HashMap<>();
    private final Map<String, UUID> _chzzkChannelIdByPlayerUID = new HashMap<>();

    public UUID getChzzkChannelIdByPlayerUID(String channelId)
    {
        return _chzzkChannelIdByPlayerUID.get(channelId);
    }

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
                _chzzkChannelIdByPlayerUID.put(newToken.getChannelId(), uuid);

                // 후원 이벤트 설정
                JSONObject settings = _chzzkChannelIdByJsonSettings.get(newToken.getChannelId());
                ChzzkSessionManager.getSession(newToken).thenAccept(session ->
                {
                    if (settings != null)
                    {
                        try
                        {
                            JSONObject donationSettings = settings.getJSONObject(DONATION_SETTINGS_KEY);
                            DonationCommandExecutor commandExecutor = new DonationCommandExecutor(donationSettings);
                            session.subscribeDonationEvent(newToken, commandExecutor, throwable ->
                            {
                                Bukkit.getScheduler().runTask(this, () ->
                                {
                                    player.sendMessage(KsiuCore.getErrorTextBuilder().append("후원 API 연동에 실패했습니다. 관리자를 호출해주세요.").build());
                                });
                            });
                        }
                        catch (Exception ex)
                        {
                            session.subscribeDonationEvent(newToken, _chzzkDefaultDonationCommands, throwable ->
                            {
                                Bukkit.getScheduler().runTask(this, () ->
                                {
                                    player.sendMessage(KsiuCore.getErrorTextBuilder().append("후원 API 연동에 실패했습니다. 관리자를 호출해주세요.").build());
                                });
                            });
                        }
                    }
                }).exceptionally(throwable ->
                {
                    Bukkit.getScheduler().runTask(this, () ->
                    {
                        getLogger().warning("세션 연동에 실패했습니다.");
                    });
                    return null;
                });
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
            _chzzkChannelIdByPlayerUID.remove(token.getChannelId());
            token.revoke();
        }
    }

    public void refreshChzzkToken(Player player)
    {
        UUID uuid = player.getUniqueId();
        ChzzkToken token = _uuidByChzzkToken.get(uuid);
        if (token == null)
            return;

        player.sendMessage(KsiuCore.getPrefixTextBuilder().append("치지직 재인증을 시작합니다...").build());
        _chzzkAuthorizer.refreshToken(token).thenAccept(newToken ->
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
            token.revoke();
        });
        _uuidByChzzkToken.clear();
        _chzzkChannelIdByPlayerUID.clear();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        removeChzzkToken(event.getPlayer());
    }

    @Override
    public void onDisable()
    {
        writeChzzkJsonSettings();
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
