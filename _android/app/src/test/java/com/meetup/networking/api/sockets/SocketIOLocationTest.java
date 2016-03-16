package com.meetup.networking.api.sockets;

import android.location.Location;
import android.location.LocationManager;

import com.meetup.errorhandling.RequestFailedException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.net.URISyntaxException;

public class SocketIOLocationTest {
    /**
     * Test properties.
     */
    private String mJwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lZmlyc3QiOiJ0ZXN0QHRlc3QubmV0IiwibmFtZWxhc3QiOm51bGwsInVzZXJJZCI6IjE4IiwiaWF0IjoxNDU3NzM2Mzk2fQ.eU3WqLSKTr-Y3_wcnxb4NWTQRNycHgfjC0uS6BTWazg";

    private static String testGroup      = "1";
    private static double testLatitude   = 0.0;
    private static double testLongitude  = 0.0;
    private static String testMessage    = "Hello world!";
    private static String testNameFirst1 = "test@test.net";

    private MeetUpGroupSocket mSenderSocket;
    private MeetUpGroupSocket mReceiverSocket;

    /**
     * Setup.
     */
    @Before
    public void beforeEach() throws URISyntaxException {
        mSenderSocket   = new MeetUpGroupSocket();
        mReceiverSocket = new MeetUpGroupSocket();

        mSenderSocket.connect();
        mReceiverSocket.connect();
    }

    /**
     * Tear down.
     */
    @After
    public void afterEach() {
        tearDownSocket(mSenderSocket);
        tearDownSocket(mReceiverSocket);
    }

    /**
     * Helper methods.
     */
    private void tearDownSocket(MeetUpGroupSocket s) {
        s.off();
        s.disconnect();
    }

    /**
     * Tests.
     */
    @Test(timeout = 2000)
    public void socketIO_SmokeTest_Success() throws JSONException, InterruptedException, URISyntaxException, RequestFailedException {
        final TestCallbacks callback = new TestCallbacks();
        mReceiverSocket.setTarget(new GroupSocketCallbacks() {
            @Override
            public void onNewMessage(JSONObject response)  {
                try {
                    assertEquals(response.getString(MeetUpGroupSocket.MESSAGE), testMessage);
                    assertEquals(response.getJSONObject(MeetUpGroupSocket.SENDER).getString(MeetUpGroupSocket.NAME_FIRST), testNameFirst1);
                    assertEquals(response.getJSONObject(MeetUpGroupSocket.SENDER).get(MeetUpGroupSocket.NAME_LAST), JSONObject.NULL);

                    callback.callMessage();
                } catch (JSONException e) {
                    fail(e.toString());
                }
            }

            @Override
            public void onNewLocation(JSONObject response) {
                try {
                    assertEquals(response.getJSONObject(MeetUpGroupSocket.LOCATION).getDouble(MeetUpGroupSocket.LATITUDE), testLatitude, 0.0010);
                    assertEquals(response.getJSONObject(MeetUpGroupSocket.LOCATION).getDouble(MeetUpGroupSocket.LONGITUDE), testLongitude, 0.0010);

                    callback.callLocation();
                } catch (Exception e) {
                    fail(e.toString());
                }
            }

            @Override
            public void onFailedLocationUpdate(JSONObject data) {
                fail("Should never failed to update location.");
            }
        });
        mSenderSocket.setTarget(new GroupSocketCallbacks() {
            @Override
            public void onNewMessage(JSONObject data) { }

            @Override
            public void onNewLocation(JSONObject data) { }

            @Override
            public void onFailedLocationUpdate(JSONObject data) { }
        });

        Location locationData = new Location(LocationManager.NETWORK_PROVIDER);
        locationData.setLongitude(testLongitude);
        locationData.setLatitude(testLatitude);

        mSenderSocket.joinRoom(testGroup);
        mReceiverSocket.joinRoom(testGroup);

        mSenderSocket.sendLocation(mJwt, testGroup, locationData);
        mSenderSocket.sendMessage(mJwt, testGroup, testMessage);
        try {
            while (!callback.isLocationCalled() || !callback.isMessageCalled()) { Thread.sleep(1); }
        } catch (Exception e) {
            fail("Threading problem.");
        }
    }

    private class TestCallbacks {
        private boolean mLocationIsCalled = false;
        private boolean mMessageIsCalled = false;

        public void callLocation() { mLocationIsCalled = true; }

        public void callMessage() { mMessageIsCalled = true; }

        public boolean isLocationCalled() { return mLocationIsCalled; }

        public boolean isMessageCalled() { return mMessageIsCalled; }

        @Override
        public String toString() {
            return String.format("isLocationCalled: %b", mLocationIsCalled);
        }
    }
}
