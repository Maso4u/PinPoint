package com.pepeta.pinpoint;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;
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
    String PreferredLandMarkType;
    String PreferredMeasuringUnitType;
    @Exclude
    PlaceFilter placeFilter;

    public Settings() {
    }

    //region GETTERS AND SETTERS
    public String getPreferredLandMarkType() {
        return PreferredLandMarkType;
    }

    public void setPreferredLandMarkType(String preferredLandMarkType) {
        PreferredLandMarkType = preferredLandMarkType;
        if (PreferredLandMarkType.equals("Natural")) placeFilter=PlaceFilter.NATURAL;
        else if (PreferredLandMarkType.equals("Purpose built/Man-made")) placeFilter= PlaceFilter.PURPOSE_BUILT;
        else if (PreferredLandMarkType.equals("Sports")) placeFilter = PlaceFilter.SPORTS;
        else if (PreferredLandMarkType.equals("Events")) placeFilter = PlaceFilter.EVENTS;
    }

    public String getPreferredMeasuringUnitType() {
        return PreferredMeasuringUnitType;
    }

    public void setPreferredMeasuringUnitType(String preferredMeasuringUnitType) {
        PreferredMeasuringUnitType = preferredMeasuringUnitType;
    }

    public PlaceFilter getPlaceFilter(){return placeFilter;}
  /*  <item>Natural</item>
    <item>Purpose built/Man-made</item>
        <item>Sports</item>
        <item>Events</item>*/
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
