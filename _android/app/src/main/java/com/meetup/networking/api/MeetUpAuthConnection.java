package com.meetup.networking.api;

import com.meetup.errorhandling.RequestFailedException;
import com.meetup.errorhandling.UserNotFoundException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class MeetUpAuthConnection extends MeetUpConnection {
    /**
     * URL strings.
     */
    private static final String AUTH     = "auth";
    private static final String LOGIN    = "login";
    private static final String REGISTER = "register";

    /**
     * Request properties.
     */
    private static final String EMAIL    = "email";
    private static final String PASSWORD = "password";

    public String login(String email, String password) throws UserNotFoundException, RequestFailedException {
        HttpURLConnection connection = null;
        String response = null;
        String url      = formatURLString(MU_API_URL, AUTH, LOGIN);

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
        String url      = formatURLString(MU_API_URL, AUTH, REGISTER);

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
        HttpURLConnection connection = super.openConnection(url, requestType);
        connection.setRequestProperty(EMAIL, email);
        connection.setRequestProperty(PASSWORD, password);

        return connection;
    }
}
