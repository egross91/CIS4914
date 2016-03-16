package com.meetup.networking.api.sockets;

import com.meetup.errorhandling.RequestFailedException;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public abstract class MeetUpSocket {
    protected static final String MU_SERVER  = "http://localhost:3000";

    public static final String JOIN_GROUP = "joingroup";
    public static final String GROUP_ID   = "groupId";

    private Socket mSocket;

    protected MeetUpSocket() throws URISyntaxException {
        this(MU_SERVER);
    }

    protected MeetUpSocket(String url) throws URISyntaxException {
        this(url, null);
    }

    protected MeetUpSocket(String url, IO.Options opts) throws URISyntaxException {
        setSocket(IO.socket(url, opts));
        initSocket();
    }

    protected void setSocket(Socket s) {
        if (s == null)
            throw new NullPointerException("Socket is null.");

        mSocket = s;
    }

    protected Socket getSocket() {
        return mSocket;
    }

    public final void disconnect() {
        doDisconnect();
    }

    /**
     * Optional to override.
     */
    protected void doDisconnect() {
        off();
        mSocket.disconnect();
    }

    public final void connect() {
        doConnect();
    }

    /**
     * Optional to override.
     */
    protected void doConnect() {
        mSocket.connect();
    }

    protected void emit(String event, JSONObject data) {
        mSocket.emit(event, data);
    }

    protected void on(String event, Emitter.Listener listener) {
        mSocket.on(event, listener);
    }

    protected void off() {
        mSocket.off();
    }

    protected void off(String event) {
        mSocket.off(event);
    }

    protected void off(String event, Emitter.Listener listener) {
        mSocket.off(event, listener);
    }

    /**
     * TODO: Add default behavior to socket.
     * i.e., Error handling, logistics, statistics.
     */
    protected void initSocket() {

    }

    /**
     * TODO: Add default behavior to socket.
     * i.e., Error handling, logistics, statistics.
     */
    protected void prepareData(JSONObject data, Object... args) throws JSONException {

    }

    public final void joinRoom(Object key) throws RequestFailedException {
        try {
            doJoinRoom(key);
        } catch (JSONException e) {
            throw new RequestFailedException(e);
        }
    }

    protected void doJoinRoom(Object key) throws JSONException {
        JSONObject data = new JSONObject();
        data.put(GROUP_ID, key);

        emit(JOIN_GROUP, data);
    }
}
