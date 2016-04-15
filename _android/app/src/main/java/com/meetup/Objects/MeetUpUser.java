package com.meetup.objects;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MeetUpUser {
    /**
     * JSON properties.
     */
    public static final String USERID    = "userid";
    public static final String USERNAME  = "username";
    public static final String NAMEFIRST = "namefirst";
    public static final String NAMELAST  = "namelast";

    /**
     * Data properties.
     */
    private int userId;
    private String nameFirst;
    private String nameLast;

    public MeetUpUser(int id, String un, String nf, String nl) {
        this.userId    = id;
        this.nameFirst = nf;
        this.nameLast  = nl;
    }

    public MeetUpUser(JSONObject jsonObj) throws JSONException {
        this.userId    = jsonObj.getInt(USERID);
        this.nameFirst = jsonObj.getString(NAMEFIRST);
        this.nameLast  = jsonObj.getString(NAMELAST);
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return (!TextUtils.isEmpty(getNameFirst())) ? getNameFirst() : getNameLast();
    }
}
