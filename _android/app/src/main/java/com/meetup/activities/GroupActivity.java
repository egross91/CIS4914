package com.meetup.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.meetup.R;
import com.meetup.applications.MeetUpApplication;
import com.meetup.fragment.GroupMemberListFragment;
import com.meetup.interfaces.Populatable;
import com.meetup.networking.api.MeetUpGroupConnection;
import com.meetup.networking.api.MeetUpUserConnection;
import com.meetup.objects.MeetUpGroup;
import com.meetup.objects.MeetUpUser;

import org.json.JSONObject;

import java.util.Collection;

public class GroupActivity extends MeetUpActivity implements Populatable {
    /**
     * Activity data members.
     */
    private MeetUpGroup mGroup;

    /**
     * Activity fragments.
     */
    private GroupMemberListFragment mMemberListFrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);


        Intent previousActivityData = getIntent();
        String groupIdStr           = previousActivityData.getStringExtra(MeetUpApplication.GROUPID_KEY);
        long groupId                = Long.parseLong(groupIdStr);
        mMemberListFrag             = (GroupMemberListFragment) getSupportFragmentManager().findFragmentById(R.id.group_member_fragment);

        populate(groupId);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getGroup() != null) {
            populate(getGroupId());
        }
    }

    @Override
    public void populate(Object... objs) {
        long groupId = (long) objs[0];

        new GetGroupInfoTask().execute(groupId);
    }

    private MeetUpGroup getGroup() {
        return this.mGroup;
    }

    public Collection<MeetUpUser> getGroupMembers() {
        return getGroup().getGroupMembers();
    }

    public long getGroupId() {
        return Long.parseLong(getGroup().getId());
    }

    public void addGroupMember(MeetUpUser user) {
        getGroup().addGroupMember(user);
    }

    public void clearGroupMembers() {
        getGroup().clearGroupMembers();
    }

    private void setGroup(MeetUpGroup group) {
        this.mGroup = group;
    }

    private class GetGroupInfoTask extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... params) {
            long groupId = params[0];
            MeetUpGroupConnection groupInfoConnection = new MeetUpGroupConnection(MeetUpUserConnection.MU_API_URL, getJwt());

            try {
                JSONObject groupInfoJson = groupInfoConnection.getGroupInfo(groupId);
                setGroup(new MeetUpGroup(groupInfoJson));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch_group), Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        public void onPostExecute(Void nothing) {
            mMemberListFrag.populate();
        }
    }

    @Override
    public String toString() {
        return "GroupAct.toString()";
    }
}