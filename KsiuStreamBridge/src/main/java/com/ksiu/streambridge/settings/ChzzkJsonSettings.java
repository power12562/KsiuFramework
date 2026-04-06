package com.ksiu.streambridge.settings;

import com.ksiu.commons.shadow.org.json.JSONException;
import com.ksiu.commons.shadow.org.json.JSONObject;
import com.ksiu.streambridge.KsiuStreamBridge;

import javax.annotation.Nullable;

public class ChzzkJsonSettings
{
    public ChzzkJsonSettings()
    {
        clear();
    }

    public ChzzkJsonSettings(@Nullable JSONObject jsonRoot) throws JSONException
    {
        if (jsonRoot == null)
        {
            clear();
        }
        else
        {
            _jsonRoot = jsonRoot;
            _chatSettings = _jsonRoot.getJSONObject(KsiuStreamBridge.CHAT_SETTINGS_KEY);
            _donationSettings = _jsonRoot.getJSONObject(KsiuStreamBridge.DONATION_SETTINGS_KEY);
            _subscriptionSettings = _jsonRoot.getJSONObject(KsiuStreamBridge.SUBSCRIPTION_SETTINGS_KEY);
        }
    }

    public void clear()
    {
        _jsonRoot = new JSONObject();
        _chatSettings = new JSONObject();
        _donationSettings = new JSONObject();
        _subscriptionSettings = new JSONObject();

        _jsonRoot.put(KsiuStreamBridge.CHAT_SETTINGS_KEY, _chatSettings);
        _jsonRoot.put(KsiuStreamBridge.DONATION_SETTINGS_KEY, _donationSettings);
        _jsonRoot.put(KsiuStreamBridge.SUBSCRIPTION_SETTINGS_KEY, _subscriptionSettings);
    }

    private JSONObject _jsonRoot;

    public JSONObject getJsonRoot()
    {
        return _jsonRoot;
    }

    private JSONObject _chatSettings;

    public JSONObject getChatSettings()
    {
        return _chatSettings;
    }

    private JSONObject _donationSettings;

    public JSONObject getDonationSettings()
    {
        return _donationSettings;
    }

    private JSONObject _subscriptionSettings;

    public JSONObject getSubscriptionSettings()
    {
        return _subscriptionSettings;
    }
}