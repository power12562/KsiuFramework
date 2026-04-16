package com.ksiu.streambridge.events.chzzk.interfaces;

import com.ksiu.commons.shadow.org.json.JSONObject;

public interface ICommandExecutor
{
    public void execute(JSONObject jsonObject);

    public JSONObject serializeToJson();

    public void deserializeFromJson(JSONObject json);
}
