package com.meetup.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.meetup.R;

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
public class UserLandingActivity extends MeetUpActivity {
    private Button mAddButton;
    private Button mRemoveButton;
    private Button mRenameButton;
    private ListView mListView;
    private ArrayList<String> groupStrArray;
    private ArrayAdapter<String> adapter;
    private EditText mEditText;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


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
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice, groupStrArray);
        mListView.setAdapter(adapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (groupStrArray.contains(mEditText.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "The group already exists!", Toast.LENGTH_LONG).show();
                } else if (mEditText.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Group cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    groupStrArray.add(mEditText.getText().toString());
                    adapter.notifyDataSetChanged();
                }

            }

        });

//        mRenameButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//               update();
//            }
//
//        });
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                delete();
            }

        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEditText.setText(groupStrArray.get(position));


            }
        });

    }
    private void delete() {


          if (mEditText.getText().toString().trim().length() != 0) {
            groupStrArray.remove(mEditText.getText().toString());
            adapter.notifyDataSetChanged();
            mEditText.setText("");
            Toast.makeText(getApplicationContext(), "Group Deleted", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Nothing to Delete", Toast.LENGTH_LONG).show();
        }

    }

//    private void update(){
//
//        String name = mEditText.getText().toString();
//        if(!name.isEmpty() && name.length()>0){
//            groupStrArray.remove(sing);
//            groupStrArray.add(pos,name);
//            adapter.notifyDataSetChanged();
//            Toast.makeText(getApplicationContext(), "Group Renamed", Toast.LENGTH_LONG).show();
//        }
//        else {
//            Toast.makeText(getApplicationContext(), "Nothing to Rename", Toast.LENGTH_LONG).show();
//        }
//
//    }



}
