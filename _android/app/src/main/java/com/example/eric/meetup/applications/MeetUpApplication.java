package com.example.eric.meetup.applications;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.eric.meetup.activities.MeetUpActivity;

/**
 * This class is meant to represent the state of the Application
 * that the user is currently in. Mostly meant for holding
 * and passing the JWT that will be used in requests to
 * the MeetUp API.
 */
public class MeetUpApplication extends Application {
    private static final String SP_FILE_NAME = "MeetUp.info";

    private String mJwt;
    private static final String JWT_KEY = "TRY_TO_KEEP_LE_SECRET";

    private MeetUpActivity mCurrentActivity;

    public synchronized void setMeetUpActivity(Activity muActivity) {
        mCurrentActivity = (MeetUpActivity) muActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void onPause() {
        // This might be used in the future.
        if (!TextUtils.isEmpty(getJwt())) {
            SharedPreferences.Editor editor = getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE).edit();
            Log.wtf("MeetUpApp.onPause()", String.format("JWT: %s", getJwt()));

            editor.putString(JWT_KEY, getJwt());
            editor.apply();
        }
    }

    public void onResume() {
        // This might be used in the future.
        String storedJwt = getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE).getString(JWT_KEY, "");

        if (!TextUtils.isEmpty(storedJwt)) {
            setJwt(storedJwt);
        }

        // TODO: Check if user's JWT is good - if not, LoginLanding; else, UserLanding.
    }

    private boolean isSessionOk() {
        // TODO: Check if user's JWT is good.
        throw new UnsupportedOperationException("This method is yet to be implemented.");
    }

    public void setJwt(String jwt) {
        mJwt = jwt;
    }

    public String getJwt() {
        return mJwt;
    }
}
