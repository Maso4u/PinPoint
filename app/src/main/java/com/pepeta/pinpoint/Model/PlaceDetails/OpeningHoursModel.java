package com.pepeta.pinpoint.Model.PlaceDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpeningHoursModel {

    //region Object Fields
    @SerializedName("open_now")
    @Expose
    private Boolean openNow;
    @SerializedName("periods")
    @Expose
    private List<PeriodModel> periods = null;
    @SerializedName("weekday_text")
    @Expose
    private List<String> weekdayText = null;
    //endregion

    //region GETTERS AND SETTERS
    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<PeriodModel> getPeriods() {
        return periods;
    }

    public void setPeriods(List<PeriodModel> periods) {
        this.periods = periods;
    }

    public List<String> getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(List<String> weekdayText) {
        this.weekdayText = weekdayText;
    }
    //endregion


}