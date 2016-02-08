package com.example.eric.meetup.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eric.meetup.R;

import java.util.ArrayList;

/**
 * Created by Kun on 2/6/16.
 */

/**After registering User is brought to list of group page
 User has the option to remove or delete a group he/she has created or are apart of.
 After double clicking a group user is brought to a friends list that is associated with that group
 User is then able to request additional friends to join the group
 *
 * */
public class UserLandingActivity extends MeetUpActivity{
    private Button mAddButton;
    private ListView mListView;
    private ArrayList<String> groupStrArray;
    private ArrayAdapter<String> adapter;
    private EditText mEditText;


    // TODO the ability to highlight a listview to remove
    // TODO ability for selected and pressed to carry onto friends that belong in that group


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_landing_activity);
        mAddButton = (Button) findViewById(R.id.add_Button);
        mListView = (ListView) findViewById(R.id.group_ListView);
        mEditText = (EditText) findViewById(R.id.edit_Text);


        groupStrArray = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, groupStrArray);
        mListView.setAdapter(adapter);
        mAddButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                
                if(groupStrArray.contains(mEditText.getText().toString())){
                    Toast.makeText(getApplicationContext(), "The group already exists!", Toast.LENGTH_LONG).show();
                }
                else if(mEditText.getText().toString().trim().length()==0){
                    Toast.makeText(getApplicationContext(), "Group cannot be empty!", Toast.LENGTH_LONG).show();
                }
                else{
                    groupStrArray.add(mEditText.getText().toString());
                    adapter.notifyDataSetChanged();
                }

            }

        });
    }



}
