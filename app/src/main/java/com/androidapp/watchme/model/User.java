package com.androidapp.watchme.model;


import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {

    public String Name;
    public String Email;
    public String type;
    public String Buddy;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String type, String buddyEmail) {
        this.Name = name;
        this.Email = email;
        this.type = type;
        this.Buddy = buddyEmail;
    }

}
