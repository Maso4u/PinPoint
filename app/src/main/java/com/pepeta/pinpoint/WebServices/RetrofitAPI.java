package com.pepeta.pinpoint.WebServices;

import com.pepeta.pinpoint.Model.NearByPlaces.GoogleNearbyPlacesModel;
import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;
import com.pepeta.pinpoint.Model.PlaceDetails.PlaceDetailsRootModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("/maps/api/place/nearbysearch/json")
    Observable<GoogleNearbyPlacesModel> getNearByPlaces(
            @Query("keyword") String keyword,
            @Query("location") String location,
            @Query("radius") Integer radius,
            @Query("type") String type,
            @Query("key") String apiKey
    );
    @GET("/maps/api/place/details/json")
    Observable<PlaceDetailsRootModel> getPlaceDetails(
            @Query("place_id") String placeId,
            @Query("key") String apiKey
    );

}
