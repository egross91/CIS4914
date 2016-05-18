package com.meetup.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.meetup.R;
import com.meetup.activities.GroupActivity;
import com.meetup.adapter.MeetUpUserAdapter;
import com.meetup.interfaces.Populatable;
import com.meetup.networking.api.MeetUpGroupConnection;
import com.meetup.objects.MeetUpGroup;
import com.meetup.objects.MeetUpUser;

import org.json.JSONArray;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberListFragment extends ListFragment implements Populatable {
    private MeetUpUserAdapter mGroupMembersAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.group_member_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private String getJwt() {
        return ((GroupActivity) getActivity()).getJwt();
    }

    @Override
    public void populate(Object... objs) {
        GroupActivity parentActivity = (GroupActivity) getActivity();

        if (parentActivity != null) {
            long groupId = parentActivity.getGroupId();

//        new GetGroupInfoTask().execute(groupId);
            new GetGroupMembersTask().execute(groupId);
        }
    }

    private class GetGroupMembersTask extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... params) {
            long groupId                     = params[0];
            GroupActivity parentActivity     = (GroupActivity) getActivity();
            MeetUpGroupConnection connection = new MeetUpGroupConnection(getJwt());
            JSONArray groupMembersFromDb     = null;

            try {
                parentActivity.clearGroupMembers();
                groupMembersFromDb = connection.getGroupMembers(groupId);

                for (int i = 0; i < groupMembersFromDb.length(); ++i) {
                    parentActivity.addGroupMember(new MeetUpUser(groupMembersFromDb.getJSONObject(i)));
                }

                List<MeetUpUser> members = new ArrayList<MeetUpUser>(parentActivity.getGroupMembers());
                mGroupMembersAdapter = null;
                mGroupMembersAdapter = new MeetUpUserAdapter(parentActivity, members);
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), getString(R.string.failed_to_fetch_members), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        public void onPostExecute(Void nothing) {
            setListAdapter(mGroupMembersAdapter);
        }
    }
}
