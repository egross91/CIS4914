package com.meetup.networking.api;

import android.text.TextUtils;

import com.meetup.errorhandling.RequestFailedException;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

public class MeetUpUserConnection extends MeetUpMiddlewareConnection {
    /**
     * Property strings.
     */
    protected static final String FRIENDIDS    = "friendids";
    protected static final String GROUPMEMBERS = "groupmembers";
    protected static final String GROUPNAME    = "groupName";
    protected static final String GROUPDESC    = "groupDesc";

    /**
     * Endpoint strings.
     */
    private static final String USER    = "user";
    private static final String FRIENDS = "friends";
    private static final String GROUPS  = "groups";
    private static final String GROUP   = "group";
    private static final String MEMBERS = "members";
    private static final String INFO    = "info";
    private static final String DEL     = "delete";

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
        String url = formatURLString(getUrl(), USER, GROUPS);

        try {
            openConnection(url);
            connect();

            int statusCode = getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(getErrorStream()));
            else if (statusCode == HttpURLConnection.HTTP_NO_CONTENT)
                return null;

            return new JSONArray(getResponse());
        } catch (JSONException e) {
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

    public JSONArray getGroup(int groupId) throws IOException, RequestFailedException {
        String url = formatURLString(getUrl(), USER, GROUP, Integer.toString(groupId));

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
            return null;
        } finally {
            disconnect();
        }
    }

    public boolean deleteGroup(int groupId) throws IOException, RequestFailedException {
        String url = formatURLString(getUrl(), USER, GROUP, DEL, Integer.toString(groupId));

        try {
            openConnection(url, POST);
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(getErrorStream()));
            else if (responseCode == HttpURLConnection.HTTP_OK)
                return true;


            return Boolean.parseBoolean(getResponse());
        } finally {
            disconnect();
        }
    }

    public boolean updateGroupMembers(int groupId, List<Integer> idsOfGroupMembers) throws RequestFailedException {
        String url = formatURLString(getUrl(), USER, MEMBERS, GROUP, UPDATE, Integer.toString(groupId));

        try {
            openConnection(url, POST);
            setRequestProperty(GROUPMEMBERS, formatArray(',', idsOfGroupMembers.toArray()));
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException("Failed to update group members.");

            return (!TextUtils.isEmpty(getResponse()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
        }
    }

    public boolean updateGroupInfo(int groupId, String groupName, String groupDesc) throws RequestFailedException {
        String url = formatURLString(getUrl(), USER, GROUP, INFO, UPDATE, Integer.toString(groupId));

        try {
            openConnection(url, POST);
            setRequestProperty(GROUPNAME, groupName);
            setRequestProperty(GROUPDESC, groupDesc);
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException("Failed to update group info.");

            return (!TextUtils.isEmpty(getResponse()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnect();
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
