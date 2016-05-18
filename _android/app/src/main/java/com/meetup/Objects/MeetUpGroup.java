package com.meetup.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MeetUpGroup {
    private String id;
    private String name;
    private String description;
    private List<MeetUpUser> groupMembers = new ArrayList<MeetUpUser>();

    public static final String GROUP_ID = "groupid";
    public static final String GROUP_NAME = "groupname";
    public static final String GROUP_DESCRIPTION = "groupdescription";

    public MeetUpGroup(String name, String id, String desc){
        this.name        = name;
        this.id          = id;
        this.description = desc;
    }

    public MeetUpGroup(JSONObject jsonObject) throws JSONException {
        this.id          = jsonObject.getString(GROUP_ID);
        this.name        = jsonObject.getString(GROUP_NAME);
        this.description = jsonObject.getString(GROUP_DESCRIPTION);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Collection<MeetUpUser> getGroupMembers() {
        return Collections.unmodifiableCollection(groupMembers);
    }

    public boolean addGroupMember(MeetUpUser member) {
        return this.groupMembers.add(member);
    }

    public boolean addGroupMembers(List<MeetUpUser> members) {
        for (MeetUpUser member : members) {
            addGroupMember(member);
        }

        return true;
    }

    public boolean removeGroupMember(MeetUpUser member) {
        return this.groupMembers.remove(member);
    }

    public boolean removeGroupMembers(List<MeetUpUser> members) {
        for (MeetUpUser member : members) {
            removeGroupMember(member);
        }

        return true;
    }

    public void clearGroupMembers() {
        this.groupMembers.clear();
    }
}
