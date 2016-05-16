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
    private final int mThirdGroup  = 3;

    /**
     * JSON properties/
     */
    private static final String USERID   = "userid";
    private static final String FRIENDID = "friendid";


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
    public void meetUpUserConnection_GetMe_ReturnsUserInfo() throws Exception {
        // Setup.
        MeetUpUserConnection connection = new MeetUpUserConnection(HOST_NAME, mJwt);

        // Execute.
        JSONObject me = connection.getCurrentUser();

        // Assert.
        assertEquals(me.getInt(USERID), mFirstUser);
    }
}
