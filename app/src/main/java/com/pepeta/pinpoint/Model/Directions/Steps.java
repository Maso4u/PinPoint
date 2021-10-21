package com.pepeta.pinpoint.Model.Directions;
public class Steps
{
    private Distance distance;

    private Duration duration;

    private EndLocation endLocation;

    private String htmlInstructions;

    private Polyline polyline;

    private StartLocation startLocation;

    private String travelMode;

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
    public void setEndLocation(EndLocation endLocation){
        this.endLocation = endLocation;
    }
    public EndLocation getEndLocation(){
        return this.endLocation;
    }
    public void setHtmlInstructions(String htmlInstructions){
        this.htmlInstructions = htmlInstructions;
    }
    public String getHtmlInstructions(){
        return this.htmlInstructions;
    }
    public void setPolyline(Polyline polyline){
        this.polyline = polyline;
    }
    public Polyline getPolyline(){
        return this.polyline;
    }
    public void setStartLocation(StartLocation startLocation){
        this.startLocation = startLocation;
    }
    public StartLocation getStartLocation(){
        return this.startLocation;
    }
    public void setTravelMode(String travelMode){
        this.travelMode = travelMode;
    }
    public String getTravelMode(){
        return this.travelMode;
    }
}

