package com.ksiu.streambridge.gui;

import com.ksiu.core.KsiuCore;
import com.ksiu.core.builders.ItemBuilder;
import com.ksiu.gui.dialog.DialogInputInteger;
import com.ksiu.gui.dialog.DialogInputString;
import com.ksiu.gui.manager.KsiuGUIStack;
import com.ksiu.gui.virtualInventory.VirtualInventoryGUIBase;
import com.ksiu.streambridge.KsiuStreamBridge;
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
        //TODO: 숲 연동하기 버튼
        setItem(3, ItemBuilder.newBuilder(Material.LAPIS_BLOCK).setName("숲 연동하기").build(), null);

        _dialogInteger = new DialogInputInteger("금액 설정하기", "금액", (player, value) ->
        {
            _amount = Integer.max(0, value);
        });
        // 금액 설정 버튼
        setItem(3 + 9, ItemBuilder.newBuilder(Material.GOLD_BLOCK).setName("금액 설정하기").build(), event ->
        {
            KsiuGUIStack.push((Player) event.getWhoClicked(), _dialogInteger);
        });

        _dialogString = new DialogInputString("명령어 설정하기", "명령어", (player, string) ->
        {
            _command = string.replaceFirst("^/", "");
        });
        // 명령어 설정 버튼
        setItem(4 + 9, ItemBuilder.newBuilder(Material.COMMAND_BLOCK).setName("명령어 설정하기").build(), event ->
        {
            KsiuGUIStack.push((Player) event.getWhoClicked(), _dialogString);
        });

        //TODO: 적용 버튼
        setItem(5 + 9, ItemBuilder.newBuilder(Material.ACACIA_LEAVES).setName("적용하기").build(), event ->
        {
            event.getWhoClicked().sendMessage(KsiuCore.getPrefixTextBuilder().append("금액: ").append(String.valueOf(_amount)).build());
            event.getWhoClicked().sendMessage(KsiuCore.getPrefixTextBuilder().append("명령어: ").append(_command).build());
        });
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
        //TODO:API 연동 여부에 따라 에메랄드블록/레드스톤블록
        ItemBuilder builder = ItemBuilder.newBuilder(isConnect ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        builder.setName(isConnect ? "API 연동중입니다." : "API를 연동해주세요.");
        return builder.build();
    }

    @Override
    public void onOpen(InventoryOpenEvent event)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        Player player = (Player) event.getPlayer();
        //TODO: 이후 SOOP 여부로 바꿔야함
        setItem(0, getConnectedItem(player, false), null);

        boolean isConnectChzzk = isConnectChzzk(player);
        setItem(5, ItemBuilder.newBuilder(Material.OBSIDIAN).setName("치지직 연동하기").build(), !isConnectChzzk ? clickEvent ->
        {
            KsiuStreamBridge br = KsiuStreamBridge.getInstance();
            br.authorizerChzzk(player);
            KsiuGUIStack.popOrClose(player, this);
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
}
