package com.pepeta.pinpoint.Model.Directions;
import java.util.ArrayList;
import java.util.List;

public class GeocodedWaypoints
{
    private String geocoderStatus;

    private String place_id;

    private List<String> types;

    public void setGeocoderStatus(String geocoderStatus){
        this.geocoderStatus = geocoderStatus;
    }
    public String getGeocoderStatus(){
        return this.geocoderStatus;
    }
    public void setPlace_id(String place_id){
        this.place_id = place_id;
    }
    public String getPlace_id(){
        return this.place_id;
    }
    public void setTypes(List<String> types){
        this.types = types;
    }
    public List<String> getTypes(){
        return this.types;
    }
}
