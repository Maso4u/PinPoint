package com.pepeta.pinpoint.Model.Directions;

import java.util.List;
public class Legs
{
    private Distance distance;

    private Duration duration;

    private String endAddress;

    private EndLocation endLocation;

    private String startAddress;

    private StartLocation startLocation;

    private List<Steps> steps;

    private List<String> trafficSpeedEntry;

    private List<String> viaWaypoint;

    public void setDistance(Distance distance){
        this.distance = distance;
    }
    public Distance getDistance(){
        return this.distance;
    }
    public void setDuration(Duration duration){
        this.duration = duration;
    }
    public Duration getDuration(){
        return this.duration;
    }
    public void setEndAddress(String endAddress){
        this.endAddress = endAddress;
    }
    public String getEndAddress(){
        return this.endAddress;
    }
    public void setEndLocation(EndLocation endLocation){
        this.endLocation = endLocation;
    }
    public EndLocation getEndLocation(){
        return this.endLocation;
    }
    public void setStartAddress(String startAddress){
        this.startAddress = startAddress;
    }
    public String getStartAddress(){
        return this.startAddress;
    }
    public void setStartLocation(StartLocation startLocation){
        this.startLocation = startLocation;
    }
    public StartLocation getStartLocation(){
        return this.startLocation;
    }
    public void setSteps(List<Steps> steps){
        this.steps = steps;
    }
    public List<Steps> getSteps(){
        return this.steps;
    }
    public void setTrafficSpeedEntry(List<String> trafficSpeedEntry){
        this.trafficSpeedEntry = trafficSpeedEntry;
    }
    public List<String> getTrafficSpeedEntry(){
        return this.trafficSpeedEntry;
    }
    public void setViaWaypoint(List<String> viaWaypoint){
        this.viaWaypoint = viaWaypoint;
    }
    public List<String> getViaWaypoint(){
        return this.viaWaypoint;
    }
}