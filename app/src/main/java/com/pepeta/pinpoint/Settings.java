package com.pepeta.pinpoint;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Settings implements Parcelable {
    public enum PlaceFilter {
        NATURAL( "natural_feature,point_of_interest,landmark", "","national%park", "beach", "cave", "hill", "mountain", "waterfall", "island", "forest"),

        PURPOSE_BUILT("airport,aquarium,art_gallery,bar,bowling_alley,park,campground,casino,rv_park,campground,embassy,hindu_temple,lodging,mosque,movie_theater,museum,park,zoo,synagogue,tourist_attraction,point_of_interest", "historic","museum","art%gallery","wildlife","park"),

        SPORTS("bicycle_store,stadium,point_of_interest",  "sport"),

        EVENTS("point_of_interest", "market","festival","parade");

        private final String[] keywords;
        private final String types;

        public String[] getKeywords() {
            return keywords;
        }

        public String getTypes() {
            return types;
        }

        PlaceFilter(String types, String... keywords) {
            this.types = types;
            this.keywords = keywords;
        }
    }

    String preferredLandMarkType;
    String preferredMeasuringUnitType;
    String mode;
    String radius;

    @Exclude
    PlaceFilter placeFilter;

    public Settings() {
    }

    //region GETTERS AND SETTERS
    public String getPreferredLandMarkType() {
        return preferredLandMarkType;
    }

    public void setPreferredLandMarkType(String preferredLandMarkType) {
        this.preferredLandMarkType = preferredLandMarkType;
        if (this.preferredLandMarkType.equals("Natural")) placeFilter=PlaceFilter.NATURAL;
        else if (this.preferredLandMarkType.equals("Purpose built/Man-made")) placeFilter= PlaceFilter.PURPOSE_BUILT;
        else if (this.preferredLandMarkType.equals("Sports")) placeFilter = PlaceFilter.SPORTS;
        else if (this.preferredLandMarkType.equals("Events")) placeFilter = PlaceFilter.EVENTS;
    }

    public String getPreferredMeasuringUnitType() {
        return preferredMeasuringUnitType;
    }

    public void setPreferredMeasuringUnitType(String preferredMeasuringUnitType) {
        this.preferredMeasuringUnitType = preferredMeasuringUnitType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public PlaceFilter getPlaceFilter(){return placeFilter;}
    //endregion

    //region Parcelable implementation
    protected Settings(Parcel in) {
        preferredLandMarkType = in.readString();
        preferredMeasuringUnitType = in.readString();
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
        dest.writeString(preferredLandMarkType);
        dest.writeString(preferredMeasuringUnitType);
    }
    //endregion

}
