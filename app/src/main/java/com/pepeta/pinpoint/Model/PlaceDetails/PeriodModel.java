package com.pepeta.pinpoint.Model.PlaceDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PeriodModel{

    @SerializedName("close")
    @Expose
    private CloseModel close;
    @SerializedName("open")
    @Expose
    private OpenModel open;

    public CloseModel getClose() {
        return close;
    }

    public void setClose(CloseModel close) {
        this.close = close;
    }

    public OpenModel getOpen() {
        return open;
    }

    public void setOpen(OpenModel open) {
        this.open = open;
    }

}