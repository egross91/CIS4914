package com.meetup.networking.api;

import com.meetup.networking.api.sockets.MeetUpGroupSocket;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class MeetUpUserConnectionTest {
    /**
     * Configuration.
     */
    private final String HOST_NAME = "localhost";

    /**
     * Test data.
     */
    private final String mJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lZmlyc3QiOiJ0ZXN0QHRlc3QubmV0IiwibmFtZWxhc3QiOiIiLCJ1c2VySWQiOiIxOCIsImlhdCI6MTQ1Njk4NTUyNn0.pgkdTCJ6kGIW2Y15uGEAJt4b495pvIFirm7FIJLhp-c";

    /**
     * Assertion values.
     */
    private final int mFirstUser   = 18;
    private final int mSecondUser  = 19;
    private final int mFirstGroup  = 1;
    private final int mSecondGroup = 2;
    private final int mThirdGroup  = 3;

    private final String mGroupName = "other";
    private final String mGroupDesc = "group";


    /**
     * JSON properties/
     */
    private static final String USERID   = "userid";
    private static final String FRIENDID = "friendid";
    private static final String GROUPID  = "groupid";

    @Test
    public void meetUpUserConnection_GetFriends_ReturnsFriends() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray friends     = connection.getFriends();
        JSONObject onlyFriend = friends.getJSONObject(0);

        // Assert.
        assertEquals(onlyFriend.getInt(FRIENDID), mSecondUser);
    }

    @Test
    public void meetUpUserConnection_GetGroup_ReturnsGroup() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray groupMembers = connection.getGroup(mFirstGroup);
        JSONObject memberOne   = groupMembers.getJSONObject(0);
        JSONObject memberTwo   = groupMembers.getJSONObject(1);

        // Assert.
        assertEquals(memberOne.getInt(USERID), mSecondUser);
        assertEquals(memberTwo.getInt(USERID), mFirstUser);
    }

    @Test
    public void meetUpUserConnection_GetGroup_ReturnsNull() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray groupMembers = connection.getGroup(-1); // Should never exist.

        // Assert.
        assertNull(groupMembers);
    }

    @Test
    public void meetUpUserConnection_GetGroups_ReturnsGroups() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray userGroups = connection.getGroups();

        // Assert.
        assertEquals(userGroups.getJSONObject(0).getInt(GROUPID), mFirstGroup);
        assertEquals(userGroups.getJSONObject(1).getInt(GROUPID), mSecondGroup);
    }

    @Test
    public void meetUpUserConnection_UpdateGroupInfo_ReturnsTrue() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        boolean updateOk = connection.updateGroupInfo(mSecondGroup, mGroupName, mGroupDesc);

        // Assert.
        assertTrue(updateOk);
    }

    @Test
    public void meetUpUserConnection_DeleteGroup_ReturnsTrue() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        boolean deleteOk = connection.deleteGroup(mThirdGroup);

        // Assert.
        assertTrue(deleteOk);
    }

    @Test
    public void meetUpUserConnection_GetNextGroupId_ReturnsThree() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        int nextGroupId = connection.getNextGroupId();

        // Assert.
        assertEquals(nextGroupId, mThirdGroup);
    }
}
