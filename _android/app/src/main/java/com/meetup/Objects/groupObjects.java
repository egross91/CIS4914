package com.meetup.Objects;

/**
 * Created by Kun on 4/12/16.
 */
public class groupObjects {
    private int id;
    private String name;
    String description;
    //ArayList<Users> members;

    public groupObjects(){
        super();
    }

    public groupObjects(String name){
        super();
        this.name = name;
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
