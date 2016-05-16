package com.meetup.networking.api;

import android.text.TextUtils;

import com.meetup.errorhandling.RequestFailedException;
import com.meetup.objects.MeetUpGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

public class MeetUpUserConnection extends MeetUpMiddlewareConnection {
    /**
     * Property strings.
     */
    protected static final String FRIENDIDS    = "friendids";

    /**
     * Endpoint strings.
     */
    private static final String ME      = "me";
    private static final String USER    = "user";
    private static final String FRIENDS = "friends";

    // Should never be used.
    private MeetUpUserConnection() {
        super(null);
        throw new RuntimeException("This constructor should never be called.");
    }

    public MeetUpUserConnection(String jwt) {
        super(jwt);
    }

    public MeetUpUserConnection(String url, String jwt) {
        super(url, jwt);
    }

    public JSONObject getCurrentUser() throws IOException, RequestFailedException {
        String url = formatURLString(getUrl(), USER , ME);

        try {
            openConnection(url);
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(getErrorStream()));

            return new JSONObject(getResponse());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect();
        }
    }

    public JSONArray getFriends() throws RequestFailedException, IOException {
        String url = formatURLString(getUrl(), USER, FRIENDS);

        try {
            openConnection(url);
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(getErrorStream()));
            else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT)
                return null;

            return new JSONArray(getResponse());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect();
        }
    }

    public boolean updateFriends(List<Integer> friendIds) throws RequestFailedException {
        String url = formatURLString(getUrl(), USER, FRIENDS, UPDATE);

        try {
            openConnection(url);
            setRequestProperty(FRIENDIDS, formatArray(',', friendIds.toArray()));
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(getErrorStream()));

            return (!TextUtils.isEmpty(getResponse()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }
}
