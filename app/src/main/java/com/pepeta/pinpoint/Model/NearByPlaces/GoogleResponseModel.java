package com.pepeta.pinpoint.Model.NearByPlaces;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleResponseModel {
    @SerializedName("result")
    @Expose
    private List<GoogleNearbyPlaceModel> googleNearbyPlaceModelList;

    public List<GoogleNearbyPlaceModel> getGooglePlaceModelList() {
        return googleNearbyPlaceModelList;
    }

    public void setGooglePlaceModelList(List<GoogleNearbyPlaceModel> googleNearbyPlaceModelList) {
        this.googleNearbyPlaceModelList = googleNearbyPlaceModelList;
    }
}
