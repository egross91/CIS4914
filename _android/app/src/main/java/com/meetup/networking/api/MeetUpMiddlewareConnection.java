package com.meetup.networking.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 * This class is to be extended for any endpoints that will hit the middleware.
 * In foundational case, every endpoint will require that the JWT is still valid.
 * Otherwise, no request will be able to go through. This class handles that.
 */
public abstract class MeetUpMiddlewareConnection extends MeetUpConnection {
    /**
     * Endpoint strings.
     */
    protected static final String UPDATE = "update";

    /**
     * Request properties.
     */
    protected static final String JWT = "jwt";

    /**
     * Data properties.
     */
    private String mJwt;

    public MeetUpMiddlewareConnection(String jwt) {
        super();
        setJwt(jwt);
    }

    public MeetUpMiddlewareConnection(String url, String jwt) {
        super(url);
        setJwt(jwt);
    }

    @Override
    public HttpURLConnection openConnection(String url, String requestType) throws MalformedURLException, IOException {
        HttpURLConnection connection = super.openConnection(url, requestType);
        connection.setRequestProperty(JWT, getJwt());

        return connection;
    }

    protected String getJwt() {
        return mJwt;
    }

    protected void setJwt(String jwt) {
        if (jwt == null)
            throw new IllegalArgumentException("JWT cannot be null.");

        mJwt = jwt;
    }
}
