package com.meetup.activities;

import android.support.v7.app.AppCompatActivity;

import com.meetup.applications.MeetUpApplication;

/**
 * Every Activity should inherit from this Activity.
 */
public abstract class MeetUpActivity extends AppCompatActivity {
    public final String getJwt() {
        MeetUpApplication app = setApplicationActivity(this);
        return app.getJwt();
    }

    protected final void setJwt(String jwt) {
        MeetUpApplication app = setApplicationActivity(this);
        app.setJwt(jwt);
    }

    protected final MeetUpApplication setApplicationActivity(MeetUpActivity muActivity) {
        MeetUpApplication app = (MeetUpApplication) getApplication();
        app.setMeetUpActivity(muActivity);

        return app;
    }
}
