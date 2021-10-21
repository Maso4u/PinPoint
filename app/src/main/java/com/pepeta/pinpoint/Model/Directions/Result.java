package com.pepeta.pinpoint.Model.Directions;
import java.util.List;
public class Result {
    private List<Route> routeList;

    private String status;
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