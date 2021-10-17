package com.pepeta.pinpoint;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;

public class User implements Parcelable {
    //region FIELDS
    @Exclude
    private String Id;

    protected User(Parcel in) {
        Id = in.readString();
        fullname = in.readString();
        email = in.readString();
        password = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public String fullname, email, password;
    //endregion


    public User() {
    }

    public User(String fullname, String email, String password, FirebaseUser firebaseUser) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        Id =firebaseUser.getUid();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(fullname);
        dest.writeString(email);
        dest.writeString(password);
    }
}
