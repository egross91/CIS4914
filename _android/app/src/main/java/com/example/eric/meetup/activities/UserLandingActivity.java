package com.example.eric.meetup.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
    private Button bt;
    private ListView lv;
    private ArrayList<String> strArr;
    private ArrayAdapter<String> adapter;
    private EditText et;

    // TODO Change font color of listsize atm is clear
    // TODO the ability to highlight a listview to remove
    // TODO prevent creation of groups with same names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_landing_activity);
        bt = (Button) findViewById(R.id.button);
        lv = (ListView) findViewById(R.id.listView);
        et = (EditText) findViewById(R.id.editText);

        strArr = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, strArr);
        lv.setAdapter(adapter);
        bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                strArr.add(et.getText().toString());
                adapter.notifyDataSetChanged();

            }
        });
    }



}
