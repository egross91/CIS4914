package com.meetup.networking.api;

import com.meetup.networking.utils.NetworkInfoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class MeetUpConnection {
    // TODO: Fix to not be localhost.
    protected static final String MU_API_URL = "10.0.2.2"; // IP to access host machine's IP address from Android device - http://juristr.com/blog/2009/10/accessing-host-machine-from-your/

    /**
     * Request type strings.
     */
    public static final String POST    = "POST";
    public static final String GET     = "GET";
    public static final String OPTIONS = "OPTIONS";
    public static final String DELETE  = "DELETE";
    public static final String PUT     = "PUT";
    public static final String TRACE   = "TRACE";
    public static final String HEAD    = "HEAD";

    /**
     * Helper strings.
     */
    protected static final String HTTP          = "http://";
    protected static final String FORWARD_SLASH = "/";
    protected static final String COLON         = ":";
    protected static final String MU_PORT       = "3000";

    /**
     * Request properties.
     */
    protected static final String IP = "IP";

    /**
     * Data properties.
     */
    private HttpURLConnection mConnection;
    private String mUrl;

    public MeetUpConnection() {
        mUrl = MU_API_URL;
    }

    public MeetUpConnection(String url) {
        mUrl = url;
    }

    public int getResponseCode() throws IOException {
        return (mConnection != null) ? mConnection.getResponseCode() : -1;
    }

    protected HttpURLConnection getConnection() {
        return mConnection;
    }

    protected String getUrl() {
        return mUrl;
    }

    protected String formatURLString(String... args) throws IllegalArgumentException {
        if (args == null || args.length == 0)
            throw new IllegalArgumentException("Cannot use empty arguments.");

        StringBuilder builder = new StringBuilder();

        builder.append(HTTP);
        builder.append(args[0]);
        builder.append(COLON);
        builder.append(MU_PORT);
        for (int i = 1; i < args.length; ++i) {
            builder.append("/");
            builder.append(args[i]);
        }

        return builder.toString();
    }

    protected HttpURLConnection openConnection(String urlString) throws MalformedURLException, IOException {
        return openConnection(urlString, "GET");
    }

    protected HttpURLConnection openConnection(String urlString, String requestType) throws MalformedURLException, IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod(requestType);
        connection.setRequestProperty(IP, NetworkInfoUtil.getIPAAddress(true));
        setConnection(connection);

        return connection;
    }

    protected String toStringInputStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }

        return builder.toString().trim();
    }

    private void setConnection(HttpURLConnection connection) {
        mConnection = connection;
    }
}
