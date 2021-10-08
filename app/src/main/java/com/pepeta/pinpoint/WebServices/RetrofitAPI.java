package com.pepeta.pinpoint.WebServices;

import com.pepeta.pinpoint.Model.GooglePlacesModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RetrofitAPI {
    @GET("/maps/api/place/nearbysearch/json?location")
    Observable<GooglePlacesModel> getNearByPlaces(
            @Query("location") String location,
            @Query("radius") int radius,
            @Query("type") String type,
            @Query("apikey") String apiKey
    );
}
