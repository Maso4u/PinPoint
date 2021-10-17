package com.pepeta.pinpoint;

import android.os.Parcel;
import android.os.Parcelable;

public class Settings implements Parcelable {
    String PreferredLandMarkType;
    String PreferredMeasuringUnitType;

    public Settings() {
    }

    //region GETTERS AND SETTERS
    public String getPreferredLandMarkType() {
        return PreferredLandMarkType;
    }

    public void setPreferredLandMarkType(String preferredLandMarkType) {
        PreferredLandMarkType = preferredLandMarkType;
    }

    public String getPreferredMeasuringUnitType() {
        return PreferredMeasuringUnitType;
    }

    public void setPreferredMeasuringUnitType(String preferredMeasuringUnitType) {
        PreferredMeasuringUnitType = preferredMeasuringUnitType;
    }
    //endregion

    //region Parcelable implementation
    protected Settings(Parcel in) {
        PreferredLandMarkType = in.readString();
        PreferredMeasuringUnitType = in.readString();
    }

    public static final Creator<Settings> CREATOR = new Creator<Settings>() {
        @Override
        public Settings createFromParcel(Parcel in) {
            return new Settings(in);
        }

        @Override
        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PreferredLandMarkType);
        dest.writeString(PreferredMeasuringUnitType);
    }
    //endregion

}
