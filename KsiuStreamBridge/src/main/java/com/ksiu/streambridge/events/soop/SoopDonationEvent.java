package com.ksiu.streambridge.events.soop;

import com.ksiu.commons.shadow.org.json.JSONObject;
import com.ksiu.commons.streamconnector.soop.session.interfaces.IDonationEvent;
import com.ksiu.streambridge.KsiuStreamBridge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public final class SoopDonationEvent implements IDonationEvent
{
    private Map<String, String> _executeCommands;
    private final UUID _playerUID;

    public SoopDonationEvent(UUID playerUID)
    {
        _executeCommands = new TreeMap<>();
        _playerUID = playerUID;
    }

    public SoopDonationEvent(UUID playerUID, JSONObject jsonCommands)
    {
        deserializeFromJson(jsonCommands);
        _playerUID = playerUID;
    }

    public SoopDonationEvent(UUID playerUID, TreeMap<String, String> commands)
    {
        setExecuteCommands(commands);
        _playerUID = playerUID;
    }

    @Override
    public void execute(String nickname, String msg, int payAmount)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        try
        {
            String command = _executeCommands.get(String.valueOf(payAmount));
            Player player = Bukkit.getPlayer(_playerUID);
            if (player != null)
            {
                String playerName = player.getName();
                String finalCommand = command.replace("%player%", playerName).replace("%donator%", nickname);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            }
        }
        catch (Exception ex)
        {
            sb.getLogger().warning("잘못된 후원 이벤트 입니다.");
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
