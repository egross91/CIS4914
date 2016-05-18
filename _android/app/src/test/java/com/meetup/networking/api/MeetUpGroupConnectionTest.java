package com.meetup.networking.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MeetUpGroupConnectionTest {
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
    private final long mFirstGroup  = 1;
    private final long mSecondGroup = 2;
    private final long mThirdGroup  = 3;

    private final String mGroupName = "other";
    private final String mGroupDesc = "group";

    /**
     * JSON properties.
     */
    private static final String USERID   = "userid";
    private static final String GROUPID  = "groupid";

    @Test
    public void meetUpGroupConnection_GetGroupMembers_ReturnsGroup() throws Exception {
        // Setup.
        MeetUpGroupConnection connection = new MeetUpGroupConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray groupMembers = connection.getGroupMembers(mFirstGroup);
        JSONObject memberOne   = groupMembers.getJSONObject(0);
        JSONObject memberTwo   = groupMembers.getJSONObject(1);

        // Assert.
        assertEquals(memberOne.getInt(USERID), mSecondUser);
        assertEquals(memberTwo.getInt(USERID), mFirstUser);
    }

    @Test
    public void meetUpGroupConnection_GetGroupMembers_ReturnsNull() throws Exception {
        // Setup.
        MeetUpGroupConnection connection = new MeetUpGroupConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray groupMembers = connection.getGroupMembers(-1); // Should never exist.

        // Assert.
        assertNull(groupMembers);
    }

    @Test
    public void meetUpGroupConnection_GetGroups_ReturnsGroups() throws Exception {
        // Setup.
        MeetUpGroupConnection connection = new MeetUpGroupConnection(HOST_NAME, mJwt);

        // Execute.
        JSONArray userGroups = connection.getGroups();

        // Assert.
        assertEquals(userGroups.getJSONObject(0).getInt(GROUPID), mFirstGroup);
        assertEquals(userGroups.getJSONObject(1).getInt(GROUPID), mSecondGroup);
    }

    @Test
    public void meetUpGroupConnection_UpdateGroupInfo_ReturnsTrue() throws Exception {
        // Setup.
        MeetUpGroupConnection connection = new MeetUpGroupConnection(HOST_NAME, mJwt);

        // Execute.
        boolean updateOk = connection.updateGroupInfo(mSecondGroup, mGroupName, mGroupDesc);

        // Assert.
        assertTrue(updateOk);
    }

    @Test
    public void meetUpGroupConnection_DeleteGroup_ReturnsTrue() throws Exception {
        // Setup.
        MeetUpGroupConnection connection = new MeetUpGroupConnection(HOST_NAME, mJwt);

        // Execute.
        boolean deleteOk = connection.deleteGroup(mThirdGroup);

        // Assert.
        assertTrue(deleteOk);
    }

    @Test
    public void meetUpGroupConnection_GetNextGroupId_ReturnsThree() throws Exception {
        // Setup.
        MeetUpGroupConnection connection = new MeetUpGroupConnection(HOST_NAME, mJwt);

        // Execute.
        long nextGroupId = connection.getNextGroupId();

        // Assert.
        assertEquals(nextGroupId, mThirdGroup);
    }
}
