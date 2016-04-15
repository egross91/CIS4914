package com.meetup.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.meetup.R;
import com.meetup.objects.MeetUpUser;

import java.util.List;

public class MeetUpUserAdapter extends ArrayAdapter<MeetUpUser> {
    private static class ViewHolder {
        TextView nameTextView;
    }

    private List<MeetUpUser> groupMembers;

    public MeetUpUserAdapter(Context context, List<MeetUpUser> members) {
        super(context, android.R.layout.simple_list_item_1, members);
        this.groupMembers = members;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder viewHolder = null;
//
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_member_row, parent, false);
//
//            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.group_member_name_text_view);
//            convertView.setTag(viewHolder);
//        }
//        else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        MeetUpUser user = groupMembers.get(position);
//        viewHolder.nameTextView.setText(user.toString());
//
//        return parent;
//    }
}
