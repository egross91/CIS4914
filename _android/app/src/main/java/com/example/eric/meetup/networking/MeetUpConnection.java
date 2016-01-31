package com.example.eric.meetup.networking;

import android.app.DownloadManager;

import com.example.eric.meetup.errorhandling.RequestFailedException;
import com.example.eric.meetup.errorhandling.UserNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MeetUpConnection {
    // TODO: Fix to not be localhost.
    private static final String MU_API_URL        = "http://192.168.1.103:3000/auth/";
    private static final String LOGIN             = "login";
    private static final String REGISTER          = "register";

    private HttpURLConnection mConnection;

    public MeetUpConnection() { }

    public boolean login(String email, String password) throws UserNotFoundException, RequestFailedException {
        try {
            mConnection = getConnection(MU_API_URL + LOGIN);

            mConnection.setDoOutput(true); // POST request.
            mConnection.setRequestProperty("email", email);
            mConnection.setRequestProperty("password", password);
            mConnection.connect();

            int statusCode = mConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST)
                throw new UserNotFoundException(String.format("%s not found.", email));
            else if (statusCode != HttpURLConnection.HTTP_OK)
                throw new RequestFailedException(toStringStream(mConnection.getInputStream()));
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (mConnection != null)
                mConnection.disconnect();
        }

        return true;
    }

    public boolean register(String email, String password) {
        try {
            mConnection = getConnection(MU_API_URL + REGISTER);

            mConnection.setDoOutput(true);
            mConnection.setRequestProperty("email", email);
            mConnection.setRequestProperty("password", password);
            mConnection.connect();

            int statusCode = mConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK)
                throw new RequestFailedException(String.format("Failed to register %s.", email));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (mConnection != null)
                mConnection.disconnect();
        }

        return true;
    }

    public int getResponseCode() throws IOException {
        return (mConnection != null) ? mConnection.getResponseCode() : -1;
    }

    private HttpURLConnection getConnection(String urlString) throws Exception {
        return (HttpURLConnection) new URL(urlString).openConnection();
    }

    private String toStringStream(InputStream in) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }

        return builder.toString();
    }
}
