package com.pepeta.pinpoint.Fragments;

import static android.content.ContentValues.TAG;
import static com.pepeta.pinpoint.Constants.MAPVIEW_BUNDLE_KEY;
import static com.pepeta.pinpoint.Constants.PREFERRED_RADIUS;
import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;
import com.pepeta.pinpoint.BuildConfig;
import com.pepeta.pinpoint.Constants;
import com.pepeta.pinpoint.Model.Directions.Route;
import com.pepeta.pinpoint.Model.NearByPlaces.GoogleNearbyPlaceModel;
import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.Settings;
import com.pepeta.pinpoint.WebServices.RetrofitAPI;
import com.pepeta.pinpoint.WebServices.RetrofitClient;
import com.pepeta.pinpoint.databinding.FragmentMapsBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MapsFragment extends Fragment {
    FragmentMapsBinding binding;
    PlaceInfoWindowFragment placeInfoWindow;
    FusedLocationProviderClient mFusedLocationProviderClient;
//    String placeAddress;
    Location currentLocation;
    LatLng currentLatLng;
    GoogleMap mGoogleMap;
    DetailsModel mClickedPlace;
    String userID;
    Settings settings;
    private DatabaseReference dbSettings;

    private final RetrofitAPI googleMapsService;
    private final CompositeDisposable compositeDisposable;
    private List<GoogleNearbyPlaceModel> googleNearbyPlaceModelList;
    private List<LatLng> polylineList;
    private String placeType;
    private String[] keywords;
    private static final String ARG_USER_ID = "userID";
    String distance;
    String duration;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
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
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.setOnMarkerClickListener(marker -> {
                getClickedPlace(marker);
                binding.btnNavigate.setChecked(false);
                return true;
            });
            mFusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(requireActivity());
            try {
                mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getDeviceLocation: found location");
                        currentLocation = (Location) task.getResult();
                        googleMap.setMyLocationEnabled(true);
                        getUserSettings();
                        if (currentLocation!=null){
                            currentLatLng = new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude());
                            moveCamera(currentLatLng,googleMap);
                        }

                    } else {
                        Log.d(TAG, "getDeviceLocation: location null");
                        showMessageErrorSnackBar(binding.locationsFragmentLayout, "location null", true);
                    }
                });

            }catch (SecurityException e){
                Log.e(TAG,"getDeviceLocation: SecurityException: "+e.getMessage());
            }
        }
    };

    /*private void geoLocate(){
        Log.d(TAG,"geoLocate: geolocating");
        if (!placeAddress.isEmpty()){
            Geocoder geocoder = new Geocoder(this.getContext());
        }
    }*/

    public MapsFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getRetrofitClient();
        googleMapsService = retrofit.create(RetrofitAPI.class);
    }
    public static MapsFragment newInstance(String userID) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(ARG_USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater,container,false);

        getPLaceTypes();
        initGoogleMap(savedInstanceState);
        googleNearbyPlaceModelList = new ArrayList<>();
        return binding.getRoot();
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

    //region HELPER METHODS

    /**
     * initializes google map
     * @param savedInstanceState bundle of saved instance
     */
    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        binding.mapView.onCreate(mapViewBundle);
        binding.mapView.getMapAsync(callback);
        mClickedPlace = new DetailsModel();
        binding.btnNavigate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mClickedPlace!=null){
                if (isChecked){
                    getDirection();
                }
                else getPlaces();
            }
        });
    }

    /**
     * retrieve user settings from from database
     */
    private void getUserSettings() {
        settings = new Settings();
        dbSettings.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot settingsSnapshot: snapshot.getChildren()) {
                        String setting = Objects.requireNonNull(settingsSnapshot.getValue()).toString();
                        if (Objects.equals(settingsSnapshot.getKey(), "preferredLandMarkType")){
                            settings.setPreferredLandMarkType(setting);
                            switch (settings.getPlaceFilter()){
                                case SPORTS:
                                    placeType = Settings.PlaceFilter.SPORTS.getTypes();
                                    keywords = Settings.PlaceFilter.SPORTS.getKeywords();
                                    break;
                                case PURPOSE_BUILT:
                                    placeType = Settings.PlaceFilter.PURPOSE_BUILT.getTypes();
                                    keywords = Settings.PlaceFilter.PURPOSE_BUILT.getKeywords();
                                    break;
                                case EVENTS:
                                    placeType= Settings.PlaceFilter.EVENTS.getTypes();
                                    keywords= Settings.PlaceFilter.EVENTS.getKeywords();
                                    break;
                                case NATURAL:
                                    placeType= Settings.PlaceFilter.NATURAL.getTypes();
                                    keywords= Settings.PlaceFilter.NATURAL.getKeywords();
                                    break;
                            }
                        }
                        if(Objects.equals(settingsSnapshot.getKey(),"preferredMeasuringUnitType")) settings.setPreferredMeasuringUnitType(setting);
                        if (settingsSnapshot.getKey().equals("mode")) settings.setMode(setting);
                        if (settingsSnapshot.getKey().equals("radius")) settings.setRadius(Integer.parseInt(setting));

                        getPlaces();
                    }
                    if (settings.getRadius()<0){
                        settings.setRadius(PREFERRED_RADIUS[0]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Move camera on maps to location
     * @param latLng latitude and longitude of location
     * @param googleMap the google map
     */
    private void moveCamera(LatLng latLng, GoogleMap googleMap) {
        Log.d(TAG,"moveCamera: moving the camera to: lat: "+latLng.latitude+", lng: "+latLng.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM));
    }

    private void getPLaceTypes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbReference = database.getReference();
        dbSettings = dbReference.child(Constants.NODE_SETTINGS);
//        arrPlaceTypes = new ArrayList<>();
    }

    /**
     * retrieve google places from API
     */
    private void getPlaces(){
        String strCurrentLocation = currentLatLng.latitude+","+currentLatLng.longitude;
        // Make the call using Retrofit and RxJava
        if (keywords!=null){
            for (String keyword: keywords) {
                compositeDisposable.add(
                        googleMapsService.getNearByPlaces(
                                keyword,
                                strCurrentLocation,
                                settings.getRadius(),
                                placeType,
                                BuildConfig.MAPS_API_KEY)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        GooglePlacesModel ->{
                                            if (GooglePlacesModel.getPlaceModels()!=null){
                                                googleNearbyPlaceModelList.addAll(GooglePlacesModel.getPlaceModels());
                                                showPlaces();
                                            }
                                        },
                                        throwable -> Log.d("MYERROR", "accept: " + throwable.getMessage())
                                )
                );
            }
        }
    }

    /**
     * displays google places found from the api
     */
    private void showPlaces() {
        if (googleNearbyPlaceModelList.size()>0){
            Set<GoogleNearbyPlaceModel> placeModelSet = new HashSet<>(googleNearbyPlaceModelList);
            googleNearbyPlaceModelList = new ArrayList<>(placeModelSet);
            mGoogleMap.clear();
            for(GoogleNearbyPlaceModel googleNearbyPlaceModel : googleNearbyPlaceModelList){
                addMarker(googleNearbyPlaceModel, googleNearbyPlaceModelList.indexOf(googleNearbyPlaceModel));
            }
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
        Objects.requireNonNull(mGoogleMap.addMarker(markerOptions)).setTag(position);
    }

    /**
     * produces bitmap for custom marker
     * @return custom bitmap for marker
     */
    private BitmapDescriptor getCustomIcon() {
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location);
        assert background != null;
        background.setTint(getResources().getColor(R.color.green, null));
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Get the place where the clicked marker is
     * @param marker google maps marker clicked
     */
    private void getClickedPlace(Marker marker){
        LatLng markerLatLng = marker.getPosition();
        for (GoogleNearbyPlaceModel place:
                googleNearbyPlaceModelList) {
            LatLng placeLatLng = new LatLng(place.getGeometry().getLocation().getLat(),
                    place.getGeometry().getLocation().getLng());
            if (placeLatLng.equals(markerLatLng)) getPlaceDetails(place.getPlaceId());
        }
    }

    /**
     * Get details of a place
     * @param placeId google maps API placeID
     */
    private void getPlaceDetails(String placeId) {
        compositeDisposable.add(
                googleMapsService.getPlaceDetails(
                        placeId,
                        BuildConfig.MAPS_API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                placeDetailsRootModel -> displayDetailsWindow(placeDetailsRootModel.getDetails()),
                                throwable -> Log.d("MYERROR", "accept: " + throwable.getMessage())
                        )
        );
    }

    /**
     * Display the details window. in the fragment
     * @param placeDetailsModel place to display details for
     */
    private void displayDetailsWindow(DetailsModel placeDetailsModel){
        if (!placeDetailsModel.equals(mClickedPlace)){
            mClickedPlace = placeDetailsModel;
            placeInfoWindow=PlaceInfoWindowFragment.newInstance(mClickedPlace,userID);
        }else{
            placeInfoWindow = PlaceInfoWindowFragment
                    .newInstance(mClickedPlace,userID,distance,duration);
        }
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.placeInfoFragment, placeInfoWindow).commit();
        binding.placeInfoFragment.setVisibility(View.VISIBLE);
        binding.btnNavigate.setVisibility(View.VISIBLE);
    }

    /**
     * gets directions to current clicked location
     */
    private void getDirection() {
        String origin = currentLatLng.latitude+","+currentLatLng.longitude;
        String placeID = String.format("place_id:%1$s",mClickedPlace.getPlaceId());
        compositeDisposable.add(
                googleMapsService.getDirection(
                        settings.getPreferredMeasuringUnitType(),
                        settings.getMode(),
                        "less_walking",
                        origin,
                        placeID,
                        BuildConfig.MAPS_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    //if there are routes found
                    if (result.getRouteList()!=null){
                        //mGoogleMap.clear();
                        showPlaces();
                        polylineList = new ArrayList<>();
                        List<Route> routeList = result.getRouteList();

                        // Initialize shortest route variable the value of the first route
                        Route shortestRoute = routeList.get(0);
                        int shortestDuration=shortestRoute.getLegs().get(0).getDuration().getValue();

                        //region VARIABLES FOR VALUES TO DISPLAY TO USER
                        String polyline;
                        //endregion

                        //Iterate through routes found to find the shortest
                        for (Route route: routeList) {
                            int routeDuration = route.getLegs().get(0).getDuration().getValue();

                    /*compare current route duration with the shortest routeDuration
                    if current route duration is less then assign it to the shortest route*/
                            if (routeDuration<shortestDuration) shortestRoute = route;
                        }

                        //region ASSIGN SHORTEST ROUTES VALUES TO PROPERTIES TO DISPLAY
                        polyline = shortestRoute.getOverviewPolyline().getPoints();
                        distance = shortestRoute.getLegs().get(0).getDistance().getText();
                        duration = shortestRoute.getLegs().get(0).getDuration().getText();
                        polylineList = PolyUtil.decode(polyline);
//                            polylineList.addAll(decodePoly(polyline));
                        //endregion

                        //region WRITE THE SHORTEST ROUTE'S INFO IN THE INFO WINDOW
                        placeInfoWindow.binding.tvDistance.setText(
                                String.format(getString(R.string.distance_text),
                                        distance));
                        placeInfoWindow.binding.tvDuration.setText(
                                String.format(getString(R.string.duration_text),
                                        duration));

                        //endregion

                        drawPolylines();
                    }
                }, throwable -> Log.d("MYERROR", "accept: " + throwable.getMessage()))
        );
    }

    /**
     * Draws the polylines of the route
     */
    private void drawPolylines() {
        PolylineOptions polylineOptions = new PolylineOptions();
        int colorPrimary = ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.text_black);

        polylineOptions.color(colorPrimary);
        polylineOptions.width(10);
        polylineOptions.startCap(new ButtCap());
        polylineOptions.jointType(JointType.ROUND);
        polylineOptions.addAll(polylineList);
        mGoogleMap.addPolyline(polylineOptions);
        LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        boundBuilder.include(currentLatLng);
        boundBuilder.include(new LatLng(mClickedPlace.getGeometry().getLocation().getLat(),
                mClickedPlace.getGeometry().getLocation().getLng()));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),100));
    }

    //endregion
}