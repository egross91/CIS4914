package com.example.eric.meetup.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class MeetUpConnection {
    // TODO: Fix to not be localhost.
    protected static final String MU_API_URL = "http://192.168.1.103:3000/auth/";

    public static final String POST    = "POST";
    public static final String GET     = "GET";
    public static final String OPTIONS = "OPTIONS";
    public static final String DELETE  = "DELETE";
    public static final String PUT     = "PUT";
    public static final String TRACE   = "TRACE";
    public static final String HEAD    = "HEAD";

    private HttpURLConnection mConnection;

    public MeetUpConnection() { }

    public int getResponseCode() throws IOException {
        return (mConnection != null) ? mConnection.getResponseCode() : -1;
    }

    protected HttpURLConnection getConnection() {
        return mConnection;
    }

    protected HttpURLConnection openConnection(String urlString) throws MalformedURLException, IOException {
        return openConnection(urlString, "GET");
    }

    protected HttpURLConnection openConnection(String urlString, String requestType) throws MalformedURLException, IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod(requestType);
        setConnection(connection);

        return connection;
    }

    protected String toStringInputStream(InputStream in) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }

        return builder.toString();
    }

    private void setConnection(HttpURLConnection connection) {
        mConnection = connection;
    }
}
