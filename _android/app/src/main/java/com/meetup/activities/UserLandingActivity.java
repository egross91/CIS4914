package com.meetup.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.meetup.networking.api.MeetUpConnection;
import com.meetup.networking.api.MeetUpGroupConnection;
import com.meetup.objects.MeetUpGroup;
import com.meetup.R;
import com.meetup.helpers.OnDoubleTapHelper;
import com.meetup.networking.api.MeetUpUserConnection;
import com.meetup.objects.MeetUpUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * After registering User is brought to list of group page
 * User has the option to remove or delete a group he/she has created or are apart of.
 * After double clicking a group user is brought to a friends list that is associated with that group
 * User is then able to request additional friends to join the group
 */
//TODO make remove dependent on selection rather than name in editText
public class UserLandingActivity extends MeetUpActivity  {
    private Button mAddButton;
    private Button mRemoveButton;
    private Button mRenameButton;
    private ListView mListView;
    private ArrayList<MeetUpGroup> mGroupList;
    private ArrayAdapter<MeetUpGroup> mGroupAdapter;
    private EditText mEditText;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    // TODO the ability to highlight a listview to remove
    // TODO ability for selected and pressed to carry onto friends that belong in that group

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_landing_activity);
        mAddButton = (Button) findViewById(R.id.add_Button);
        mRemoveButton = (Button) findViewById(R.id.remove_Button);
        mListView = (ListView) findViewById(R.id.group_ListView);
        mEditText = (EditText) findViewById(R.id.edit_Text);
        mRenameButton = (Button) findViewById(R.id.Rename);

        mGroupList = new ArrayList<MeetUpGroup>();

        mGroupAdapter = new ArrayAdapter<MeetUpGroup>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, mGroupList);
        mListView.setAdapter(mGroupAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                add();
            }
        });
        mRenameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                update();
            }

        });
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                delete();
            }

        });

        mListView.setOnTouchListener(new OnDoubleTapHelper(this){
            public void onDoubleTap(MotionEvent e){
                Toast.makeText(getApplicationContext(), "DoubleTap", Toast.LENGTH_LONG).show();
                Intent groupListActivity = new Intent(UserLandingActivity.this, GroupListActivity.class);
                startActivity(groupListActivity);
           }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditText.setText(mGroupList.get(position).getName());
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onResume() {
        super.onResume();

        new PopulateUserGroupsTask().execute();
    }

    private void add() {
        String groupName = mEditText.getText().toString().trim();

        if (groupNameExists(groupName)) {
            Toast.makeText(getApplicationContext(), "The group already exists!", Toast.LENGTH_LONG).show();
        } else if (groupName.length() == 0) {
            Toast.makeText(getApplicationContext(), "Group cannot be empty!", Toast.LENGTH_LONG).show();
        } else {
            new AddGroupAsyncTask().execute(groupName);
        }

        mEditText.setText("");
    }

    private void delete() {
        int groupIndex = mListView.getCheckedItemPosition();

        if (groupIndex > -1) {
            MeetUpGroup groupToDelete = mGroupList.get(groupIndex);

            new DeleteGroupTask().execute(groupToDelete.getId());
        } else {
            Toast.makeText(getApplicationContext(), "Nothing to Delete", Toast.LENGTH_LONG).show();
        }

        mEditText.setText("");
    }

    private void update() {
        String groupName = mEditText.getText().toString().trim();

        if (groupName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Nothing to Rename", Toast.LENGTH_LONG).show();
        } else if (groupNameExists(groupName)) {
            Toast.makeText(getApplicationContext(), "Group Name Exists!", Toast.LENGTH_LONG).show();
        } else {
            MeetUpGroup group = mGroupList.get(mListView.getCheckedItemPosition());

            UpdateGroupInfoTask updateGroupTask = new UpdateGroupInfoTask();
            updateGroupTask.setGroupId(group.getId());
            updateGroupTask.setGroupName(groupName);
            updateGroupTask.setGroupDesc(group.getDescription());

            updateGroupTask.execute(group);
        }

        mEditText.setText("");
    }

    private boolean groupNameExists(final String groupName) {
        for (MeetUpGroup group : mGroupList) {
            if (groupName.equals(group.getName()))
                return true;
        }

        return false;
    }

    private List<Integer> convertMembersToListOfInts(Collection<MeetUpUser> members) {
        List<Integer> ids = new ArrayList<Integer>();

        for (MeetUpUser member : members)
            ids.add(member.getUserId());

        return ids;
    }

    /**
     * TASKS
     */
    private class PopulateUserGroupsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            MeetUpGroupConnection connection = new MeetUpGroupConnection(MeetUpUserConnection.MU_API_URL, getJwt());

            try {
                JSONArray userGroups = connection.getGroups();

                if (userGroups != null){
                    for(int i = 0; i < userGroups.length(); i++){
                        mGroupList.add(new MeetUpGroup(userGroups.getJSONObject(i)));
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGroupAdapter.notifyDataSetChanged();
                    }
                });
            } catch(Exception e){
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_fetch_groups), Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
    }

    private class AddGroupAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String groupName = params[0];

            MeetUpGroupConnection groupIdConnection  = new MeetUpGroupConnection(MeetUpUserConnection.MU_API_URL, getJwt());
            MeetUpGroupConnection addGroupConnection = new MeetUpGroupConnection(MeetUpUserConnection.MU_API_URL, getJwt());
            MeetUpGroupConnection grpMembsConnection = new MeetUpGroupConnection(MeetUpConnection.MU_API_URL, getJwt());
            MeetUpUserConnection userInfoConnection  = new MeetUpUserConnection(MeetUpConnection.MU_API_URL, getJwt());

            try {
                long groupId = groupIdConnection.getNextGroupId();
                MeetUpUser me = new MeetUpUser(userInfoConnection.getCurrentUser());
                final MeetUpGroup newGroup = new MeetUpGroup(groupName, Long.toString(groupId), "");

                newGroup.addGroupMember(me);
                addGroupConnection.updateGroupInfo(newGroup);
                grpMembsConnection.updateGroupMembers(Long.parseLong(newGroup.getId()), convertMembersToListOfInts(newGroup.getGroupMembers()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGroupList.add(newGroup);
                        mGroupAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_add_group), Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
    }

    private class DeleteGroupTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String groupId = params[0];

            MeetUpGroupConnection deleteGroupConnection = new MeetUpGroupConnection(MeetUpUserConnection.MU_API_URL, getJwt());

            try {
                deleteGroupConnection.deleteGroup(Long.parseLong(groupId));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGroupList.remove(mListView.getCheckedItemPosition());
                        mGroupAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "Group Deleted", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_delete_group), Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }
    }

    private class UpdateGroupInfoTask extends AsyncTask<MeetUpGroup, Void, Void> {
        private String grpId;
        private String grpName;
        private String grpDesc;

        @Override
        protected Void doInBackground(MeetUpGroup[] params) {
            MeetUpGroupConnection updateNameConnection = new MeetUpGroupConnection(MeetUpUserConnection.MU_API_URL, getJwt());

            try {
                MeetUpGroup group = params[0];
                updateNameConnection.updateGroupInfo(Integer.parseInt(getGroupId()), getGroupName(), getGroupDesc());
                updateGroup(group);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGroupAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), getString(R.string.group_renamed_successfully), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.failed_to_update_group_info), Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        private void updateGroup(MeetUpGroup group) {
            group.setName(getGroupName());
            group.setDescription(getGroupDesc());
        }

        public String getGroupId() {
            return grpId;
        }

        public void setGroupId(String grpId) {
            this.grpId = grpId;
        }

        public String getGroupName() {
            return grpName;
        }

        public void setGroupName(String grpName) {
            this.grpName = grpName;
        }

        public String getGroupDesc() {
            return grpDesc;
        }

        public void setGroupDesc(String grpDesc) {
            this.grpDesc = grpDesc;
        }
    }
}
