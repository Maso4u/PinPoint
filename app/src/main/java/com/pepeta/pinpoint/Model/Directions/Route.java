package com.pepeta.pinpoint.Model.Directions;
import java.util.List;
public class Route
{

    private String copyrights;

    private List<Legs> legs;

    private OverviewPolyline overviewPolyline;

    private String summary;

    private List<String> warnings;

    private List<String> waypointOrder;

    public void setCopyrights(String copyrights){
        this.copyrights = copyrights;
    }
    public String getCopyrights(){
        return this.copyrights;
    }
    public void setLegs(List<Legs> legs){
        this.legs = legs;
    }
    public List<Legs> getLegs(){
        return this.legs;
    }
    public void setOverviewPolyline(OverviewPolyline overviewPolyline){
        this.overviewPolyline = overviewPolyline;
    }
    public OverviewPolyline getOverviewPolyline(){
        return this.overviewPolyline;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }
    public String getSummary(){
        return this.summary;
    }
    public void setWarnings(List<String> warnings){
        this.warnings = warnings;
    }
    public List<String> getWarnings(){
        return this.warnings;
    }
    public void setWaypointOrder(List<String> waypointOrder){
        this.waypointOrder = waypointOrder;
    }
    public List<String> getWaypointOrder(){
        return this.waypointOrder;
    }
}

