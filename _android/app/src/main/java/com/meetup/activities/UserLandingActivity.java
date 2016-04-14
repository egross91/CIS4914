package com.meetup.activities;

import android.content.Intent;
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
import com.meetup.objects.groupObjects;
import com.meetup.R;
import com.meetup.errorhandling.RequestFailedException;
import com.meetup.helpers.OnDoubleTapHelper;
import com.meetup.networking.api.MeetUpUserConnection;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Kun on 2/6/16.
 */

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
    private ArrayList<String> groupStrArray;
    private ArrayList<groupObjects> groupObjectArray;
    private ArrayList<JSONObject> JSarray;
    private ArrayAdapter<groupObjects> adapter;
    private EditText mEditText;
    int count = 0;





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

        groupStrArray = new ArrayList<String>();
        groupObjectArray = new ArrayList<groupObjects>();
        JSarray = new ArrayList<JSONObject>();


        adapter = new ArrayAdapter<groupObjects>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, groupObjectArray);
        mListView.setAdapter(adapter);
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
                mEditText.setText(groupObjectArray.get(position).getName());

            }
        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }




    public void populateUserGroupList() throws JSONException {
        MeetUpUserConnection connection = new MeetUpUserConnection(MeetUpUserConnection.MU_API_URL, getJwt());
        try {
            JSONArray userGroups = connection.getGroups();
            if (userGroups != null){
                int len = userGroups.length();
                for(int i = 0;i < len; i ++){
                    groupObjectArray.add(new groupObjects(userGroups.getJSONObject(i)));
                }
            }
        }catch(RequestFailedException e){
            e.printStackTrace();

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void add(){
        MeetUpUserConnection connection = new MeetUpUserConnection(MeetUpUserConnection.MU_API_URL, getJwt());
        if (groupStrArray.contains(mEditText.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "The group already exists!", Toast.LENGTH_LONG).show();
            mEditText.setText("");
        } else if (mEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Group cannot be empty!", Toast.LENGTH_LONG).show();
            mEditText.setText("");
        } else {
            groupObjectArray.add(new groupObjects(mEditText.getText().toString().trim()));

            groupStrArray.add(mEditText.getText().toString().trim());
            mEditText.setText("");
            adapter.notifyDataSetChanged();
        }

    }

    private void delete() {
        if (mListView.getCheckedItemPosition() > -1) {
            groupObjectArray.remove(mListView.getCheckedItemPosition());
            groupStrArray.remove(mListView.getCheckedItemPosition());
            adapter.notifyDataSetChanged();
            mEditText.setText("");
            Toast.makeText(getApplicationContext(), "Group Deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Nothing to Delete", Toast.LENGTH_LONG).show();
        }
        mEditText.setText("");
    }

    private void update() {

        String name = mEditText.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Nothing to Rename", Toast.LENGTH_LONG).show();
        } else if (groupStrArray.contains(name)) {
            Toast.makeText(getApplicationContext(), "Group Name Exists!", Toast.LENGTH_LONG).show();
        } else {
            groupStrArray.set(mListView.getCheckedItemPosition(), name);
            groupObjectArray.get(mListView.getCheckedItemPosition()).setName(name);
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Group Renamed", Toast.LENGTH_LONG).show();
            mEditText.setText("");

        }
    }


}





