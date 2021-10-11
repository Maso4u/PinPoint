package com.pepeta.pinpoint.Fragments;

import static android.content.ContentValues.TAG;
import static com.pepeta.pinpoint.Constants.DEFAULT_ZOOM;
import static com.pepeta.pinpoint.Constants.MAPVIEW_BUNDLE_KEY;
import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pepeta.pinpoint.BuildConfig;
import com.pepeta.pinpoint.Model.NearByPlaces.GoogleNearbyPlaceModel;
import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.WebServices.RetrofitAPI;
import com.pepeta.pinpoint.WebServices.RetrofitClient;
import com.pepeta.pinpoint.databinding.FragmentMapsBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MapsFragment extends Fragment {
    FragmentMapsBinding binding;
    FusedLocationProviderClient mFusedLocationProviderClient;
    String placeAddress;
    Location currentLocation;
    GoogleMap mGoogleMap;
    DetailsModel mClickedPlace;

    private final RetrofitAPI googleMapsService;
    private final CompositeDisposable compositeDisposable;
    private int radius =6000;
    private List<GoogleNearbyPlaceModel> googleNearbyPlaceModelList;

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
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    getClickedPlace(marker);
                    return true;
                }
            });
            mFusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(getActivity());
            try {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "getDeviceLocation: found location");
                            currentLocation = (Location) task.getResult();
                            googleMap.setMyLocationEnabled(true);
                            getPlaces("restaurant");
                            moveCamera(
                                    new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, googleMap);
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

    /**
     * Move camera on maps to location
     * @param latLng latitude and longitude of location
     * @param zoom rate to zoom on map in
     * @param googleMap the google map
     */
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
        googleNearbyPlaceModelList = new ArrayList<>();
        return binding.getRoot();
    }

    /**
     * initializes google map
     * @param savedInstanceState
     */
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

    /**
     * retrieve google places from API
     * @param type type of location to retrieve from API
     */
    private void getPlaces(String type){
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        String strCurrentLocation = latLng.latitude+","+latLng.longitude;
        // Make the call using Retrofit and RxJava
        compositeDisposable.add(
                googleMapsService.getNearByPlaces(
                        strCurrentLocation,
                        radius,
                        type,
                        BuildConfig.MAPS_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        GooglePlacesModel -> showPlaces(GooglePlacesModel.getPlaceModels()),
                        throwable -> Log.d("MYERROR", "accept: " + throwable.getMessage())
                )
        );
    }

    /**
     * displays google places found from the api
     * @param googleNearbyPlaceModels list of places found from API call
     */
    private void showPlaces(List<GoogleNearbyPlaceModel> googleNearbyPlaceModels) {
        if (googleNearbyPlaceModels.size()>0){
            googleNearbyPlaceModelList.clear();
            mGoogleMap.clear();
            for(GoogleNearbyPlaceModel googleNearbyPlaceModel : googleNearbyPlaceModels){
                googleNearbyPlaceModelList.add(googleNearbyPlaceModel);
                addMarker(googleNearbyPlaceModel, googleNearbyPlaceModels.indexOf(googleNearbyPlaceModel));
            }
        }else{
            mGoogleMap.clear();
            googleNearbyPlaceModelList.clear();
            radius+=1000;
//            getPlaces();
        }
    }

    /**
     * Adds markers to map on address points of places found fitting the criteria
     * @param googleNearbyPlaceModel model of place found by google
     * @param position position of place in list of places
     */
    private void addMarker(GoogleNearbyPlaceModel googleNearbyPlaceModel, int position) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(googleNearbyPlaceModel.getGeometry().getLocation().getLat(),
                        googleNearbyPlaceModel.getGeometry().getLocation().getLng()));
        markerOptions.icon(getCustomIcon());
        mGoogleMap.addMarker(markerOptions).setTag(position);
    }

    /**
     * produces bitmap for custom marker
     * @return custom bitmap for marker
     */
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

    private void getClickedPlace(Marker marker){
        LatLng markerLatLng = marker.getPosition();
        for (GoogleNearbyPlaceModel place:
                googleNearbyPlaceModelList) {
            LatLng placeLatLng = new LatLng(place.getGeometry().getLocation().getLat(),
                    place.getGeometry().getLocation().getLng());
            if (placeLatLng.equals(markerLatLng)) getPlaceDetails(place.getPlaceId());
        }
    }

    private void getPlaceDetails(String placeId) {
        compositeDisposable.add(
                googleMapsService.getPlaceDetails(
                        placeId,
                        BuildConfig.MAPS_API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                placeDetailsRootModel -> {
                                    displayDetailsWindow(placeDetailsRootModel.getDetails());
                                },
                                throwable -> Log.d("MYERROR", "accept: " + throwable.getMessage())
                        )
        );
    }
    private void displayDetailsWindow(DetailsModel placeDetailsModel){
        mClickedPlace = placeDetailsModel;
        if (mClickedPlace!=null){
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.placeInfoFragment, PlaceInfoWindowFragment.newInstance(mClickedPlace)).commit();
            binding.placeInfoFragment.setVisibility(View.VISIBLE);
        }
    }
}