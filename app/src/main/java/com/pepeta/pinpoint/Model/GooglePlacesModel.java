package com.pepeta.pinpoint.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pepeta.pinpoint.GooglePlaceModel;

import java.util.List;

public class GooglePlacesModel {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;
    @SerializedName("results")
    @Expose
    private List<GooglePlaceModel> placeModels = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<GooglePlaceModel> getPlaceModels() {
        return placeModels;
    }

    public void setPlaceModels(List<GooglePlaceModel> placeModels) {
        this.placeModels = placeModels;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
