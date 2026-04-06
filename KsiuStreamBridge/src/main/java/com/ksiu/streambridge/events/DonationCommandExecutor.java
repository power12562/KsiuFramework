package com.ksiu.streambridge.events;

import com.ksiu.commons.shadow.org.json.JSONObject;
import com.ksiu.commons.streamconnector.chzzk.session.interfaces.session.IDonationEvent;
import com.ksiu.streambridge.KsiuStreamBridge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class DonationCommandExecutor implements IDonationEvent
{
    private Map<String, String> _executeCommands;

    public DonationCommandExecutor()
    {
        _executeCommands = new TreeMap<>();
    }

    public DonationCommandExecutor(JSONObject jsonCommands)
    {
        deserializeFromJson(jsonCommands);
    }

    public DonationCommandExecutor(TreeMap<String, String> commands)
    {
        setExecuteCommands(commands);
    }

    @Override
    public void execute(JSONObject jsonObject)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        try
        {
            String channelId = jsonObject.getString("channelId");
            String payAmount = jsonObject.getString("payAmount");
            String donatorNickname = jsonObject.getString("donatorNickname");
            String command = _executeCommands.get(payAmount);
            if (command != null)
            {
                UUID playerUID = sb.getChzzkChannelIdByPlayerUID(channelId);
                Player player = Bukkit.getPlayer(playerUID);
                if (player != null)
                {
                    String finalCommand = command.replace("%player%", player.getName()).replace("%donator%", donatorNickname);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                }
            }
        }
        catch (Exception ex)
        {
            sb.getLogger().warning("[Ksiu:StreamBridge] 잘못된 후원 이벤트 입니다.");
        }
    }

    public JSONObject serializeToJson()
    {
        return new JSONObject(_executeCommands);
    }

    public void deserializeFromJson(JSONObject json)
    {
        _executeCommands = new TreeMap<>();
        json.keySet().forEach(key -> _executeCommands.put(key, json.getString(key)));
    }

    public void setExecuteCommands(TreeMap<String, String> commands)
    {
        if (commands != null)
            _executeCommands = commands;
    }
}
