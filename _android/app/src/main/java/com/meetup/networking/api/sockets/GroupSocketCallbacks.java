package com.meetup.networking.api.sockets;

import org.json.JSONObject;

public interface GroupSocketCallbacks {
    public void onNewMessage(JSONObject data);
    public void onNewLocation(JSONObject data);
    public void onFailedLocationUpdate(JSONObject data);
}