package com.meetup.networking.api.sockets;

import android.location.Location;

import com.meetup.errorhandling.RequestFailedException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.emitter.Emitter;

public class MeetUpGroupSocket extends MeetUpSocket {
    protected static final String GROUPS_ROOM = "/groups";

    /**
     * Event strings.
     */
    public static final String LOCATION_PUSH          = "locationpush";
    public static final String LOCATION_RECEIVED      = "locationreceived";
    public static final String FAILED_LOCATION_UPDATE = "failedlocationupdate";
    public static final String MESSAGE_PUSH           = "messagepush";
    public static final String MESSAGE_RECEIVED       = "messagereceived";

    /**
     * JSON properties.
     */
    public static final String LOCATION   = "location";
    public static final String LONGITUDE  = "longitude";
    public static final String LATITUDE   = "latitude";
    public static final String JWT        = "jwt";
                public static final String MESSAGE    = "message";
                public static final String NAME_FIRST = "nameFirst";
                public static final String NAME_LAST  = "nameLast";
                public static final String SENDER     = "sender";

                /**
                 * Data properties.
                 */
                private GroupSocketCallbacks mTarget;

                public MeetUpGroupSocket() throws URISyntaxException {
                    super(MU_SERVER + GROUPS_ROOM);
                }

                public MeetUpGroupSocket(String url) throws URISyntaxException {
                    super(url);
                }

                public MeetUpGroupSocket(GroupSocketCallbacks task) throws URISyntaxException {
                    super(MU_SERVER + GROUPS_ROOM);
                    setTarget(task);
                }

                public MeetUpGroupSocket(String url, GroupSocketCallbacks task) throws URISyntaxException {
                    super(url);
                    setTarget(task);
                }

            public void setTarget(GroupSocketCallbacks target) {
                mTarget = target;
            }

            protected GroupSocketCallbacks getTarget() {
                return mTarget;
            }

            public void sendMessage(String jwt, String groupId, String message) throws RequestFailedException {
                try {
                    JSONObject sendData = new JSONObject();
                    prepareData(sendData, jwt, groupId);
                    sendData.put(MESSAGE, message);

                    emit(MESSAGE_PUSH, sendData);
                } catch (JSONException e) {
                    throw new RequestFailedException(e);
                }
            }

            public void sendLocation(String jwt, String groupId, Location location) throws RequestFailedException {
                try {
                    JSONObject sendData     = new JSONObject();
                    JSONObject locationData = new JSONObject();

                    locationData.put(LONGITUDE, location.getLongitude());
                    locationData.put(LATITUDE, location.getLatitude());

                    prepareData(sendData, jwt, groupId);
                    sendData.put(LOCATION, locationData);

                    emit(LOCATION_PUSH, sendData);
                } catch (JSONException e) {
                    throw new RequestFailedException(e);
                }
            }

            @Override
            protected void initSocket() {
                super.initSocket();

                on(MESSAGE_RECEIVED, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject response = (JSONObject) args[0];

                        getTarget().onNewMessage(response);
                    }
                });

                on(LOCATION_RECEIVED, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject response = (JSONObject) args[0];

                        getTarget().onNewLocation(response);
                    }
                });

                on(FAILED_LOCATION_UPDATE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject error = (JSONObject) args[0];

                getTarget().onFailedLocationUpdate(error);
            }
        });
    }

    /**
     * Should be called before and/or after _nearly_ every network call.
     * @param data: Data to be sent.
     * @param args: Parameters for data to be prepared.
     * @throws JSONException
     */
    @Override
    protected void prepareData(JSONObject data, Object... args) throws JSONException {
        try {
            data.put(JWT, args[0]);
            data.put(GROUP_ID, args[1]);
        } catch (Exception e) { }
    }
}
