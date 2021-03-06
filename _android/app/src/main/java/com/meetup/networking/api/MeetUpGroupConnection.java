package com.meetup.networking.api;

import android.text.TextUtils;

import com.meetup.errorhandling.RequestFailedException;
import com.meetup.objects.MeetUpGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.NoSuchElementException;

public class MeetUpGroupConnection extends MeetUpMiddlewareConnection {
    /**
     * Property strings.
     */
    protected static final String GROUPMEMBERS = "groupmembers";
    protected static final String GROUPNAME    = "groupName";
    protected static final String GROUPDESC    = "groupDesc";
    protected static final String GROUPID      = "groupid";

    /**
     * Endpoint strings.
     */
    private static final String GROUPS  = "groups";
    private static final String GROUP   = "group";
    private static final String MEMBERS = "members";
    private static final String INFO    = "info";
    private static final String DEL     = "delete";
    private static final String ID      = "id";

    // Should never be used.
    private MeetUpGroupConnection() {
        super(null);
        throw new RuntimeException("This constructor should never be called.");
    }

    public MeetUpGroupConnection(String jwt) {
        super(jwt);
    }

    public MeetUpGroupConnection(String url, String jwt) {
        super(url, jwt);
    }

    public JSONArray getGroups() throws RequestFailedException, IOException {
        String url = formatURLString(getUrl(), GROUP, GROUPS);

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

    public JSONArray getGroupMembers(long groupId) throws IOException, RequestFailedException {
        String url = formatURLString(getUrl(), GROUP, MEMBERS, Long.toString(groupId));

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

    public long getNextGroupId() throws IOException, RequestFailedException {
        String url = formatURLString(getUrl(), GROUP, GROUPS, ID);

        try {
            openConnection(url);
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(toStringInputStream(getErrorStream()));

            JSONObject responseObject = new JSONObject(getResponse());

            return responseObject.getLong(GROUPID);
        } catch (JSONException e) {
            return -1;
        } finally {
            disconnect();
        }
    }

    public boolean deleteGroup(long groupId) throws IOException, RequestFailedException {
        String url = formatURLString(getUrl(), GROUP, DEL, Long.toString(groupId));

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

    public JSONObject getGroupInfo(long groupId) throws RequestFailedException, IOException {
        String url = formatURLString(getUrl(), GROUP, INFO, Long.toString(groupId));

        try {
            openConnection(url);
            connect();

            int responseCode = getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RequestFailedException(String.format("Failed to fetch group info for group[%d].", groupId));
            else if (responseCode == HttpURLConnection.HTTP_NO_CONTENT)
                throw new NoSuchElementException(String.format("No content for group[%d].", groupId));

            return new JSONObject(getResponse());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect();
        }
    }

    public boolean updateGroupMembers(long groupId, List<Integer> idsOfGroupMembers) throws RequestFailedException {
        String url = formatURLString(getUrl(), GROUP, MEMBERS, UPDATE, Long.toString(groupId));

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

    public boolean updateGroupInfo(MeetUpGroup group) throws RequestFailedException {
        return updateGroupInfo(Integer.parseInt(group.getId()), group.getName(), group.getDescription());
    }

    public boolean updateGroupInfo(long groupId, String groupName, String groupDesc) throws RequestFailedException {
        String url = formatURLString(getUrl(), GROUP, INFO, UPDATE, Long.toString(groupId));

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
}
