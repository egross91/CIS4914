package com.meetup.networking.api;

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
        assertTrue(onlyFriend.getInt(FRIENDID) == mSecondUser);
    }

    @Test
    public void meetUpUserConnection_GetGroup_ReturnsGroup() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray groupMembers = connection.getGroup(1);
        JSONObject memberOne   = groupMembers.getJSONObject(0);
        JSONObject memberTwo   = groupMembers.getJSONObject(1);

        // Assert.
        assertTrue(memberOne.getInt(USERID) == mFirstUser);
        assertTrue(memberTwo.getInt(USERID) == mSecondUser);
    }

    @Test
    public void meetUpUserConnection_GetGroups_ReturnsGroups() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray userGroups = connection.getGroups();

        // Assert.
        assertTrue(userGroups.getJSONObject(0).getInt(GROUPID) == mFirstGroup);
        assertTrue(userGroups.getJSONObject(1).getInt(GROUPID) == mSecondGroup);
    }
}