package com.meetup.networking.api;

import android.text.TextUtils;

import com.meetup.errorhandling.RequestFailedException;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class MeetUpUserConnection extends MeetUpMiddlewareConnection {
    /**
     * Property string.
     */
    protected static final String FRIENDIDS    = "friendids";
    protected static final String GROUPMEMBERS = "groupmembers";

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

    public MeetUpUserConnection(String url, String jwt) {
        super(url, jwt);
    }

    public JSONArray getGroups() throws RequestFailedException, IOException {
        HttpURLConnection connection = null;
        String response = null;
        String url      = formatURLString(getUrl(), USER, GROUPS);

        try {
            connection = openConnection(url);
            connection.connect();

            int statusCode = getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(getConnection().getErrorStream()));
            else if (statusCode == HttpURLConnection.HTTP_NO_CONTENT)
                return null;

            response = toStringInputStream(getConnection().getInputStream());

            // TODO: Format the response string into something pretty like JSON. This may work doe. *fingers crossed*
            return new JSONArray(response);
        } catch (JSONException e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public JSONArray getFriends() throws RequestFailedException, IOException {
        HttpURLConnection connection = null;
        String response = null;
        String url      = formatURLString(getUrl(), USER, FRIENDS);

        try {
            connection = openConnection(url);
            connection.connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(connection.getErrorStream()));
            else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT)
                return null;

            response = toStringInputStream(connection.getInputStream());

            return new JSONArray(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public boolean updateFriends(List<Integer> friendIds) throws RequestFailedException {
        HttpURLConnection connection = null;
        String response = null;
        String url      = formatURLString(getUrl(), USER, FRIENDS, UPDATE);

        try {
            connection = openConnection(url);
            connection.setRequestProperty(FRIENDIDS, formatArray(',', friendIds.toArray()));
            connection.connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(connection.getErrorStream()));

            response = toStringInputStream(connection.getInputStream());

            return (!TextUtils.isEmpty(response));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public JSONArray getGroup(int groupId) throws IOException, RequestFailedException {
        HttpURLConnection connection = null;
        String response = null;
        String url      = formatURLString(getUrl(), USER, GROUP, Integer.toString(groupId));

        try {
            connection = openConnection(url);
            connection.connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(connection.getErrorStream()));
            else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT)
                return null;

            response = toStringInputStream(connection.getInputStream());

            return new JSONArray(response);
        } catch (JSONException e) {
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public boolean updateGroup(int groupId, List<Integer> idsOfGroupMembers) throws RequestFailedException {
        HttpURLConnection connection = null;
        String response = null;
        String url      = formatURLString(getUrl(), USER, GROUP, UPDATE, Integer.toString(groupId));

        try {
            connection = openConnection(url);
            connection.setRequestProperty(GROUPMEMBERS, formatArray(',', idsOfGroupMembers.toArray()));
            connection.connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException("Failed to update group info.");

            response = toStringInputStream(connection.getInputStream());

            return (!TextUtils.isEmpty(response));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    private String formatArray(char delimiter, Object... args) {
        StringBuilder builder = new StringBuilder();

        if (args.length > 0) {
            builder.append(args[0]);

            for (int i = 1; i < args.length; ++i) {
                builder.append(delimiter);
                builder.append(args[i]);
            }
        }

        return builder.toString();
    }
}
