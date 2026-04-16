package com.ksiu.streambridge.gui;

import com.ksiu.commons.shadow.org.json.JSONObject;
import com.ksiu.commons.streamconnector.chzzk.token.ChzzkToken;
import com.ksiu.core.KsiuCore;
import com.ksiu.core.builders.ItemBuilder;
import com.ksiu.gui.dialog.DialogInputInteger;
import com.ksiu.gui.dialog.DialogInputString;
import com.ksiu.gui.manager.KsiuGUIStack;
import com.ksiu.gui.virtualInventory.VirtualInventoryGUIBase;
import com.ksiu.streambridge.KsiuStreamBridge;
import com.ksiu.streambridge.settings.EventJsonSettings;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class APIConnectorGUI extends VirtualInventoryGUIBase
{
    private final DialogInputInteger _dialogInteger;
    private int _amount = 0;
    private String _command;
    private final DialogInputString _dialogString;

    public APIConnectorGUI()
    {
        super("APIConnectorGUI", ESize.Size18, Component.text("API 연동하기"));
        _dialogInteger = new DialogInputInteger("금액 설정하기", "금액", (player, value) ->
        {
            _amount = Integer.max(0, value);
        });
        _dialogString = new DialogInputString("명령어 설정하기", "명령어", (player, string) ->
        {
            _command = string.replaceFirst("^/", "");
        });
    }

    private boolean isConnectSoop(Player player)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        if (sb == null)
            return false;

        return sb.hasSoopSession(player);
    }

    public boolean isConnectChzzk(Player player)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        if (sb == null)
            return false;

        return sb.hasChzzkToken(player);
    }

    public ItemStack getConnectedItem(Player player, boolean isConnect)
    {
        ItemBuilder builder = ItemBuilder.newBuilder(isConnect ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        builder.setName(isConnect ? "API 연동중입니다." : "API를 연동해주세요.");
        return builder.build();
    }

    @Override
    public void onOpen(InventoryOpenEvent event)
    {
        Player player = (Player) event.getPlayer();
        updateInventoryButtons(player);
    }

    private void updateInventoryButtons(Player player)
    {
        // API 연동 버튼
        updateAuthorizerButton(player);
        // 금액 설정 버튼
        updateAmountButton(player);
        // 명령어 설정 버튼
        updateCommandButton(player);
        //적용하기 버튼
        updateApplyButton(player);
    }

    private void updateAuthorizerButton(Player player)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        boolean isConnectSoop = isConnectSoop(player);
        setItem(0, getConnectedItem(player, isConnectSoop), isConnectSoop ? clickEvent ->
        {
            KsiuGUIStack.push(player, new DialogAPISessionControl("숲 API", extend ->
            {
                // player.sendMessage(KsiuCore.getPrefixTextBuilder().append("숲 재인증이 완료되었습니다.").build());
            }, clear ->
            {
                KsiuStreamBridge br = KsiuStreamBridge.getInstance();
                br.revokeSoopSession(player);
            }));
        } : null);
        setItem(3, ItemBuilder.newBuilder(Material.LAPIS_BLOCK).setName("숲 연동하기").build(), !isConnectSoop ? clickEvent ->
        {
            KsiuGUIStack.push(player, new DialogInputString("숲 연동하기", "방송국_ID", (clickPlayer, value) ->
            {
                KsiuStreamBridge br = KsiuStreamBridge.getInstance();
                br.authorizerSoopSession(clickPlayer, value, () -> updateInventoryButtons(player));
            }));
        } : null);

        boolean isConnectChzzk = isConnectChzzk(player);
        setItem(5, ItemBuilder.newBuilder(Material.OBSIDIAN).setName("치지직 연동하기").build(), !isConnectChzzk ? clickEvent ->
        {
            KsiuStreamBridge br = KsiuStreamBridge.getInstance();
            br.authorizerChzzk(player, () -> updateInventoryButtons(player));
        } : null);
        setItem(8, getConnectedItem(player, isConnectChzzk), isConnectChzzk ? clickEvent ->
        {
            KsiuGUIStack.push(player, new DialogAPISessionControl("치지직 API", extend ->
            {
                sb.refreshChzzkToken(player);
            }, clear ->
            {
                sb.removeChzzkToken(player);
            }));
        } : null);
    }

    private void updateAmountButton(Player player)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        boolean hasAPI = sb.hasChzzkToken(player) || sb.hasSoopSession(player);
        if (!hasAPI)
        {
            setItem(4 + 9, null, null);
        }
        else
        {
            setItem(3 + 9, ItemBuilder.newBuilder(Material.GOLD_BLOCK).setName("금액 설정하기")
                    .addLore(String.format("가격: %d", _amount))
                    .build(), clickEvent ->
            {
                KsiuGUIStack.push((Player) clickEvent.getWhoClicked(), _dialogInteger);
            });
        }
    }

    private void updateCommandButton(Player player)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        try
        {
            boolean isSoop = sb.hasSoopSession(player);
            boolean isChzzk = sb.hasChzzkToken(player);
            EventJsonSettings settings = null;
            if (isSoop)
            {
                settings = sb.getSoopEventSettings(player);
                if (settings.getDonationSettings().isEmpty())
                {
                    settings = sb.getSoopEventSettings();
                }
            }
            else if (isChzzk)
            {
                ChzzkToken token = sb.getChzzkToken(player);
                settings = sb.getChzzkEventSettings(token);
                if (settings.getDonationSettings().isEmpty())
                {
                    settings = sb.getChzzkEventSettings();
                }
            }
            String prevCommand = "null";
            if (settings != null)
            {
                JSONObject donationObject = settings.getDonationSettings();
                String amount = String.valueOf(_amount);
                if (donationObject.has(amount))
                {
                    prevCommand = donationObject.getString(amount);
                }
            }
            setItem(4 + 9, ItemBuilder.newBuilder(Material.COMMAND_BLOCK).setName("명령어 설정하기")
                    .addLore(String.format("기존 명령어: %s", prevCommand))
                    .build(), clickEvent ->
            {
                KsiuGUIStack.push((Player) clickEvent.getWhoClicked(), _dialogString);
            });
        }
        catch (Exception ex)
        {
            setItem(4 + 9, null, null);
        }
    }

    private void updateApplyButton(Player player)
    {
        final KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        boolean hasAPI = sb.hasChzzkToken(player) || sb.hasSoopSession(player);
        if (!hasAPI)
        {
            setItem(4 + 9, null, null);
        }
        else
        {
            String amountInfo = String.format("가격: %d", _amount);
            String commandInfo = String.format("새로운 명령어: %s", _command);
            ItemStack item = ItemBuilder.newBuilder(Material.ACACIA_LEAVES)
                    .setName("적용하기")
                    .addLore(amountInfo)
                    .addLore(commandInfo)
                    .build();
            setItem(5 + 9, item, clickEvent ->
            {
                // 치지직
                {
                    ChzzkToken token = sb.getChzzkToken(player);
                    if (token != null)
                    {
                        EventJsonSettings chzzkSettings = sb.getChzzkEventSettings(token);
                        chzzkSettings.getDonationSettings().put(String.valueOf(_amount), _command);
                        sb.setChzzkEventSettings(token, chzzkSettings);
                    }
                }
                // 숲
                {
                    EventJsonSettings soopSettings = sb.getSoopEventSettings(player);
                    soopSettings.getDonationSettings().put(String.valueOf(_amount), _command);
                    sb.setSoopEventSettings(player, soopSettings);
                }
                player.sendMessage(KsiuCore.getPrefixTextBuilder().append(String.format("후원 명령어 변경 -> [%d: %s]", _amount, _command)).build());
                updateCommandButton(player);
            });
        }
    }

}
