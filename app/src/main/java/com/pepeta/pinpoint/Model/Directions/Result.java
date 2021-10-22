package com.pepeta.pinpoint.Model.Directions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class Result {
    @SerializedName("routes")
    @Expose
    private List<Route> routeList;
    @SerializedName("geocoded_waypoints")
    @Expose
    private List<GeocodedWaypoints> geocodedWaypoints;
    private String status;

    public void setGeocodedWaypoints(List<GeocodedWaypoints> geocodedWaypoints){
        this.geocodedWaypoints = geocodedWaypoints;
    }
    public List<GeocodedWaypoints> getGeocodedWaypoints(){
        return this.geocodedWaypoints;
    }
    public void setRouteList(List<Route> routeList){
        this.routeList = routeList;
    }
    public List<Route> getRouteList(){
        return this.routeList;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
}