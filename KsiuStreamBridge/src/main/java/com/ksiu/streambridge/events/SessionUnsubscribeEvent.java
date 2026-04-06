package com.ksiu.streambridge.events;

import com.ksiu.commons.streamconnector.chzzk.session.interfaces.ISessionSubscribeEvent;
import com.ksiu.streambridge.KsiuStreamBridge;

public class SessionUnsubscribeEvent implements ISessionSubscribeEvent
{
    @Override
    public void execute(String eventType, String channelId)
    {
        KsiuStreamBridge sb = KsiuStreamBridge.getInstance();
        sb.getLogger().info(String.format("이벤트 해제 {%s, %s}", eventType, channelId));
    }
}
