package com.ksiu.streambridge.settings;

import com.ksiu.commons.shadow.org.json.JSONException;
import com.ksiu.commons.shadow.org.json.JSONObject;

import javax.annotation.Nullable;

public class EventJsonSettings
{
    public static final String CHAT_SETTINGS_KEY = "chat";
    public static final String DONATION_SETTINGS_KEY = "donation";
    public static final String SUBSCRIPTION_SETTINGS_KEY = "subscription";

    public EventJsonSettings()
    {
        clear();
    }

    public EventJsonSettings(@Nullable JSONObject jsonRoot) throws JSONException
    {
        if (jsonRoot == null)
        {
            clear();
        }
        else
        {
            _jsonRoot = jsonRoot;
            _chatSettings = _jsonRoot.getJSONObject(CHAT_SETTINGS_KEY);
            _donationSettings = _jsonRoot.getJSONObject(DONATION_SETTINGS_KEY);
            _subscriptionSettings = _jsonRoot.getJSONObject(SUBSCRIPTION_SETTINGS_KEY);
        }
    }

    public void clear()
    {
        _jsonRoot = new JSONObject();
        _chatSettings = new JSONObject();
        _donationSettings = new JSONObject();
        _subscriptionSettings = new JSONObject();

        _jsonRoot.put(CHAT_SETTINGS_KEY, _chatSettings);
        _jsonRoot.put(DONATION_SETTINGS_KEY, _donationSettings);
        _jsonRoot.put(SUBSCRIPTION_SETTINGS_KEY, _subscriptionSettings);
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