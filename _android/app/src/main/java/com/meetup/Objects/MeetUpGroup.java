package com.meetup.objects;

import org.json.JSONException;
import org.json.JSONObject;

public class MeetUpGroup {
    private String id;
    private String name;
    private String description;

    public static final String GROUP_ID = "groupid";
    public static final String GROUP_NAME = "groupname";
    public static final String GROUP_DESCRIPTION = "groupdescription";

    public MeetUpGroup(){
        super();
    }

    public MeetUpGroup(String name, String id, String desc){
        super();
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
}
