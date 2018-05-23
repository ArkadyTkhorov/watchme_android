package com.androidapp.watchme.model;


import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;


@IgnoreExtraProperties
public class Screenshot {

    public String Email;
    public String Date;
    public String Name;
    public Map<String, Object> timeStamp;

    public Screenshot(String email, String date, String name) {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Screenshot(String email, String date, String name, Map<String, Object> s) {
        this.Email = email;
        this.Date = date;
        this.Name = name;
        this.timeStamp = s ;

    }



}
