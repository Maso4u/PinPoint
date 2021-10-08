package com.pepeta.pinpoint.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pepeta.pinpoint.GooglePlaceModel;

import java.util.List;

public class GoogleResponseModel {
    @SerializedName("result")
    @Expose
    private List<GooglePlaceModel> googlePlaceModelList;

    public List<GooglePlaceModel> getGooglePlaceModelList() {
        return googlePlaceModelList;
    }

    public void setGooglePlaceModelList(List<GooglePlaceModel> googlePlaceModelList) {
        this.googlePlaceModelList = googlePlaceModelList;
    }
}
