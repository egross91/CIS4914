package com.meetup.objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Kun on 4/12/16.
 */
public class groupObjects {
    public String id;
    public String name;

    public static final String GROUP_ID = "groupid";
    public static final String GROUP_NAME = "groupname";
    public static final String GROUP_DESCRIPTION = "groupdescription";

    String description;
    //ArayList<Users> members;

    public groupObjects(){
        super();
    }

    public groupObjects(String name){
        super();
        this.name = name;

    }
    public groupObjects(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString(GROUP_ID);
        this.name = jsonObject.getString(GROUP_NAME);
        this.description = jsonObject.getString(GROUP_DESCRIPTION);

    }
    public String getName(){
        return name;

    }
    public void setName(String name){
        this.name = name;
    }


    @Override
    public String toString() {
        return this.name;
    }



}
