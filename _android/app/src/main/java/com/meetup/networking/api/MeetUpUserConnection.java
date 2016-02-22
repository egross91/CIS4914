package com.meetup.networking.api;

import com.meetup.errorhandling.RequestFailedException;

import org.json.JSONArray;

import java.net.HttpURLConnection;

public class MeetUpUserConnection extends MeetUpMiddlewareConnection {
    /**
     * Endpoint strings.
     */
    private static final String USER    = "user";
    private static final String FRIENDS = "friends";
    private static final String GROUPS  = "groups";
    private static final String GROUP   = "group";

    // Should never be used.
    private MeetUpUserConnection() {
        super(null);
        throw new RuntimeException("This constructor should never be called.");
    }

    public MeetUpUserConnection(String jwt) {
        super(jwt);
    }

    public JSONArray getGroups() throws Exception {
        HttpURLConnection connection = null;
        String response = null;
        String url      = formatURLString(MU_API_URL, USER, GROUPS);

        try {
            connection = openConnection(url);
            connection.connect();

            int statusCode = getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(getConnection().getErrorStream()));

            response = toStringInputStream(getConnection().getInputStream());
        } catch (Exception e) {
            // TODO: Better exception handling.
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        // TODO: Format the response string into something pretty like JSON. This may work doe. *fingers crossed*
        return new JSONArray(response);
    }
}
