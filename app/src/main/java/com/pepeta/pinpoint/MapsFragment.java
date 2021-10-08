package com.pepeta.pinpoint;

import static android.content.ContentValues.TAG;
import static com.pepeta.pinpoint.Constants.BASE_URL;
import static com.pepeta.pinpoint.Constants.DEFAULT_ZOOM;
import static com.pepeta.pinpoint.Constants.MAPVIEW_BUNDLE_KEY;
import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pepeta.pinpoint.Model.GooglePlacesModel;
import com.pepeta.pinpoint.Model.GoogleResponseModel;
import com.pepeta.pinpoint.WebServices.RetrofitAPI;
import com.pepeta.pinpoint.WebServices.RetrofitClient;
import com.pepeta.pinpoint.databinding.FragmentMapsBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapsFragment extends Fragment {
    FragmentMapsBinding binding;
    FusedLocationProviderClient mFusedLocationProviderClient;
    String placeAddress;
    Location currentLocation;

    private RetrofitAPI googleMapsService;
    private CompositeDisposable compositeDisposable;
    GoogleMap mGoogleMap;
    private int radius =5000;
    private List<GooglePlaceModel> googlePlaceModelList;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            try {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "getDeviceLocation: found location");
                            currentLocation = (Location) task.getResult();
                            googleMap.setMyLocationEnabled(true);
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, googleMap);
                            getPlaces("point_of_interest");
                        } else {
                            Log.d(TAG, "getDeviceLocation: location null");
                            showMessageErrorSnackBar(binding.locationsFragmentLayout, "location null", true);
                        }
                    }
                });

            }catch (SecurityException e){
                Log.e(TAG,"getDeviceLocation: SecurityException: "+e.getMessage());
            }

        }
    };

    private void geoLocate(){
        Log.d(TAG,"geoLocate: geolocating");
        if (!placeAddress.isEmpty()){
            Geocoder geocoder = new Geocoder(this.getContext());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, GoogleMap googleMap) {
        Log.d(TAG,"moveCamera: moving the camera to: lat: "+latLng.latitude+", lng: "+latLng.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    public MapsFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        googleMapsService = retrofit.create(RetrofitAPI.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater,container,false);
        initGoogleMap(savedInstanceState);
        googlePlaceModelList = new ArrayList<>();
//        return inflater.inflate(R.layout.fragment_maps, container, false);
        return binding.getRoot();
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        binding.mapView.onCreate(mapViewBundle);
        binding.mapView.getMapAsync(callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    private void getPlaces(String type){
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        String strCurrentLocation = latLng.latitude+","+latLng.longitude;
        // Make the call using Retrofit and RxJava
        compositeDisposable.add(googleMapsService.getNearByPlaces(
                strCurrentLocation,
                radius,
                type,
                BuildConfig.MAPS_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GooglePlacesModel>() {
                               @Override
                               public void accept(GooglePlacesModel googlePlacesModel) throws Exception {
                                   showPlaces(googlePlacesModel.getPlaceModels());
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Log.d("MYERROR", "accept: " + throwable.getMessage());
                               }
                           }
                ));

        /*compositeDisposable.add(googleMapsService.getNearByPlaces
                (
                        currentLocation,
                        radius,
                        type,
                        BuildConfig.MAPS_API_KEY)
                .enqueue(new Callback<GoogleResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<GoogleResponseModel> call, @NonNull Response<GoogleResponseModel> response) {
                        if (response.errorBody()==null){
                            if (response.body()!=null){
                                if (response.body().getGooglePlaceModelList()!=null &&
                                        response.body().getGooglePlaceModelList().size()>0){
                                    googlePlaceModelList.clear();
                                    mGoogleMap.clear();
                                     for(GooglePlaceModel googlePlaceModel : response.body().getGooglePlaceModelList()){
                                         googlePlaceModelList.add(googlePlaceModel);
                                         addMarker(googlePlaceModel);
                                     }
                                }else{
                                    mGoogleMap.clear();
                                    googlePlaceModelList.clear();
                                    radius+=1000;
                                    getPlaces(type,currentLocation);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GoogleResponseModel> call, @NonNull Throwable t) {

                    }
                });
        );*/

        /*String url = BASE_URL+"/maps/api/place/nearbysearch/json?location"
                +currentLocation.getLatitude()+","+currentLocation.getLongitude()
                +"&radius="+radius+"&type"=+placeName+"&key="
                +BuildConfig.MAPS_API_KEY;*/
    }

    private void showPlaces(List<GooglePlaceModel> googlePlaceModels) {
        if (googlePlaceModels.size()>0){
            googlePlaceModelList.clear();
            mGoogleMap.clear();
            for(GooglePlaceModel googlePlaceModel : googlePlaceModels){
                googlePlaceModelList.add(googlePlaceModel);
                addMarker(googlePlaceModel,googlePlaceModels.indexOf(googlePlaceModel));
            }
        }else{
            mGoogleMap.clear();
            googlePlaceModelList.clear();
            radius+=1000;
//            getPlaces(,currentLocation);
        }
    }

    private void addMarker(GooglePlaceModel googlePlaceModel, int position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googlePlaceModel.getGeometry().getLocation().getLat(),
                        googlePlaceModel.getGeometry().getLocation().getLng()))
                .title(googlePlaceModel.getName())
                .snippet(googlePlaceModel.getVicinity());
        markerOptions.icon(getCustomIcon());
        mGoogleMap.addMarker(markerOptions).setTag(position);
    }
    private BitmapDescriptor getCustomIcon() {
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location);
        background.setTint(getResources().getColor(R.color.green, null));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}