package com.meetup.networking.api;

import com.meetup.errorhandling.RequestFailedException;
import com.meetup.errorhandling.UserNotFoundException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class MeetUpAuthConnection extends MeetUpConnection {
    /**
     * Endpoint strings.
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
        String url = formatURLString(getUrl(), AUTH, LOGIN);

        try {
            openConnection(url, MeetUpConnection.POST, email, password);
            connect();

            int statusCode = getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST)
                throw new UserNotFoundException(String.format("%s not found.", email));
            else if (statusCode != HttpURLConnection.HTTP_OK)
                throw new RequestFailedException(toStringInputStream(getErrorStream()));
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect();
        }

        return getResponse();
    }

    public String register(String email, String password) {
        String url = formatURLString(getUrl(), AUTH, REGISTER);

        try {
            openConnection(url, MeetUpConnection.POST, email, password);
            connect();

            int statusCode = getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK)
                throw new RequestFailedException(String.format("Failed to register %s.", email));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            disconnect();
        }

        return getResponse();
    }

    private HttpURLConnection openConnection(String url, String requestType,
                                             String email, String password) throws MalformedURLException, IOException {
        super.openConnection(url, requestType);
        setRequestProperty(EMAIL, email);
        setRequestProperty(PASSWORD, password);

        return getConnection();
    }
}
