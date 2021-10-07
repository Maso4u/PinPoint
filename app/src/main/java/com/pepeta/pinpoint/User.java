package com.pepeta.pinpoint;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;

public class User {
    @Exclude
    private String Id;

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public String fullname, email, password;

    public User() {
    }

    public User(String fullname, String email, String password, FirebaseUser firebaseUser) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        Id =firebaseUser.getUid();
    }
}
