package com.example.eric.meetup.networking;

import android.text.TextUtils;
import android.util.Log;

import com.example.eric.meetup.errorhandling.RequestFailedException;
import com.example.eric.meetup.errorhandling.UserNotFoundException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class MeetUpAuthConnection extends MeetUpConnection {
    private static final String LOGIN    = "login";
    private static final String REGISTER = "register";

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    public String login(String email, String password) throws UserNotFoundException, RequestFailedException {
        HttpURLConnection connection = null;
        String response = null;
        String url      = MU_API_URL + LOGIN;

        try {
            connection = openConnection(url, MeetUpConnection.POST, email, password);
            connection.connect();

            int statusCode = getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST)
                throw new UserNotFoundException(String.format("%s not found.", email));
            else if (statusCode != HttpURLConnection.HTTP_OK)
                throw new RequestFailedException(toStringInputStream(getConnection().getErrorStream()));

            response = toStringInputStream(getConnection().getInputStream());
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return response;
    }

    public String register(String email, String password) {
        HttpURLConnection connection = null;
        String response = null;
        String url      = MU_API_URL + REGISTER;

        try {
            connection = openConnection(url, MeetUpConnection.POST, email, password);
            connection.connect();

            int statusCode = getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK)
                throw new RequestFailedException(String.format("Failed to register %s.", email));

            response = toStringInputStream(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return response;
    }

    private HttpURLConnection openConnection(String url, String requestType,
                                             String email, String password) throws MalformedURLException, IOException {
        HttpURLConnection connection = openConnection(url, requestType);
        connection.setRequestProperty(EMAIL, email);
        connection.setRequestProperty(PASSWORD, password);

        return connection;
    }
}
