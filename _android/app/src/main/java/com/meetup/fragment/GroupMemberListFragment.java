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
import com.meetup.objects.MeetUpUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberListFragment extends ListFragment implements Populatable {
    private List<MeetUpUser> mGroupMembers = new ArrayList<MeetUpUser>();
    private MeetUpUserAdapter mGroupMembersAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        int groupId = ((GroupActivity) getActivity()).getGroupId();
        new GetGroupMembersTask().execute(groupId);
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
        // TODO
    }

    private class GetGroupMembersTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int groupId                     = params[0];
            MeetUpGroupConnection connection = new MeetUpGroupConnection(getJwt());
            JSONArray groupMembersFromDb    = null;

            try {
                mGroupMembers.clear();
                groupMembersFromDb = connection.getGroup(groupId);

                for (int i = 0; i < groupMembersFromDb.length(); ++i) {
                    mGroupMembers.add(new MeetUpUser(groupMembersFromDb.getJSONObject(i)));
                }

                if (mGroupMembersAdapter != null) {
                    mGroupMembersAdapter = null;
                }

                mGroupMembersAdapter = new MeetUpUserAdapter(getActivity(), mGroupMembers);
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Failed to fetch group members.", Toast.LENGTH_SHORT).show();
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
