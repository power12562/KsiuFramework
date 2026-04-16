package com.ksiu.streambridge.events.chzzk;

import com.ksiu.commons.shadow.org.json.JSONObject;
import com.ksiu.commons.streamconnector.chzzk.session.interfaces.session.IDonationEvent;

public final class ChzzkDonationEvent implements IDonationEvent
{
    private DonationCommandExecutor _executer;

    public void setExecutor(DonationCommandExecutor executor)
    {
        _executer = executor;
    }

    public ChzzkDonationEvent(DonationCommandExecutor executor)
    {
        setExecutor(executor);
    }

    @Override
    public void execute(JSONObject jsonObject)
    {
        _executer.execute(jsonObject);
    }
}