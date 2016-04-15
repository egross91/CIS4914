package com.meetup.activities;

import android.content.Intent;
import android.os.Bundle;

import com.meetup.R;
import com.meetup.applications.MeetUpApplication;
import com.meetup.interfaces.Populatable;

public class GroupActivity extends MeetUpActivity implements Populatable {
    private int mGroupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent previousActivityData = getIntent();
        int groupId                 = previousActivityData.getIntExtra(MeetUpApplication.GROUPID_KEY, mGroupId);
        mGroupId                    = groupId;
    }

    @Override
    public void populate(Object... objs) {
        // TODO
    }

    public int getGroupId() {
        return mGroupId;
    }
}