package com.meetup.networking.api;

import android.location.Location;

import java.net.HttpURLConnection;

import com.meetup.errorhandling.RequestFailedException;

import org.json.JSONObject;

public class MeetUpLocationConnection extends MeetUpMiddlewareConnection {
    /**
     * Property string.
     */
    private static final String LATITUDE  = "latitude";
    private static final String LONGITUDE = "longitude";

    /**
     * Endpoint strings.
     */
    protected static final String LOCATION = "location";

    /**
     * Data properties.
     */

    public MeetUpLocationConnection(String jwt) {
        super(jwt);
    }

    public MeetUpLocationConnection(String url, String jwt) {
        super(url, jwt);
    }

    public Location getUserLocation(int userId) throws RequestFailedException {
        String url = formatURLString(getUrl(), LOCATION, Integer.toString(userId));

        try {
            openConnection(url);
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(String.format("Failed to get location for %d.", userId));
            else if (responseCode != HttpURLConnection.HTTP_OK)
                return null;

            JSONObject jsonResponse = new JSONObject(getResponse());
            Location location       = new Location(PROVIDER);

            location.setLatitude(jsonResponse.getDouble(LATITUDE));
            location.setLongitude(jsonResponse.getDouble(LONGITUDE));

            return location;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect();
        }
    }
}
