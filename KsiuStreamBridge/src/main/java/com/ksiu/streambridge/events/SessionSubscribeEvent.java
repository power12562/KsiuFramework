package com.ksiu.streambridge.events;

import com.ksiu.commons.streamconnector.chzzk.session.interfaces.ISessionSubscribeEvent;
import com.ksiu.streambridge.KsiuStreamBridge;

public class SessionSubscribeEvent implements ISessionSubscribeEvent
{
    @Override
    public void execute(String eventType, String channelId)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        sb.getLogger().info(String.format("이벤트 구독 {%s, %s}", eventType, channelId));
    }
}
