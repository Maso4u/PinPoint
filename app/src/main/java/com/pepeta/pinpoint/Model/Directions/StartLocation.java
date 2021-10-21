package com.pepeta.pinpoint.Model.Directions;

public class StartLocation
{
    private double lat;

    private double lng;

    public void setLat(double lat){
        this.lat = lat;
    }
    public double getLat(){
        return this.lat;
    }
    public void setLng(double lng){
        this.lng = lng;
    }
    public double getLng(){
        return this.lng;
    }
}