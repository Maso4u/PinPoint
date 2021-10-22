package com.pepeta.pinpoint.Fragments;

import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pepeta.pinpoint.Constants;
import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.databinding.FragmentPlaceInfoWindowBinding;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceInfoWindowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceInfoWindowFragment extends Fragment {
    FragmentPlaceInfoWindowBinding binding;
    SwipeListener swipeListener;

    //region ARG KEY CONSTANTS
    private static final String ARG_DETAILS = "details";
    private static final String ARG_USER_ID = "userID";
    private static final String ARG_DISTANCE = "distance" ;
    private static final String ARG_DURATION = "duration";
    //endregion

    //region ARGUMENTS FIELDS
    private String userId;
    private String distance;
    private String duration;
    private DetailsModel details;
    //endregion
    private DatabaseReference dbFavourites;
    //endregion

    public PlaceInfoWindowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param detailsModel Place to display details for
     * @return A new instance of fragment PlaceInfoWindowFragment.
     */
    public static PlaceInfoWindowFragment newInstance(DetailsModel detailsModel, String userID) {
        PlaceInfoWindowFragment fragment = new PlaceInfoWindowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID,userID);
        args.putParcelable(ARG_DETAILS,detailsModel);
        fragment.setArguments(args);
        return fragment;
    }
    public static PlaceInfoWindowFragment newInstance(DetailsModel detailsModel, String userID, String distance, String duration) {
        PlaceInfoWindowFragment fragment = new PlaceInfoWindowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID,userID);
        args.putString(ARG_DISTANCE,distance);
        args.putString(ARG_DURATION,duration);
        args.putParcelable(ARG_DETAILS,detailsModel);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            distance =getArguments().getString(ARG_DISTANCE);
            duration =getArguments().getString(ARG_DURATION);
            details = getArguments().getParcelable(ARG_DETAILS);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaceInfoWindowBinding.inflate(inflater,container,false);

        if (details!=null){
            //Initializes firebase database reference fields
            //region FIREBASE DATABASE AND REFERENCES
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbReference = database.getReference();
            dbFavourites= dbReference.child(Constants.NODE_FAVOURITES);

            //populate textviews with respective data from place details
            binding.tvPlaceName.setText(details.getName());
            binding.tvPlaceAddress.setText(details.getFormattedAddress());
            binding.tvPlaceWebsiteUrl.setText(details.getWebsite());
            binding.tvRating.setText(String.format(getString(R.string.rating_text),details.getRating()));
            binding.tvPlaceWebsiteUrl.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(details.getWebsite()))));
            binding.tvContactNumber.setText(String.format(getString(R.string.contact_number_text),details.getFormattedPhoneNumber()));
            if (duration!=null){
                binding.tvDuration.setText(String.format(getString(R.string.duration_text),duration));
            }else{
                binding.tvDuration.setText(String.format(getString(R.string.duration_text),"--"));
            }
            if (distance!=null){
                binding.tvDistance.setText(String.format(getString(R.string.distance_text),distance));
            }else{
                binding.tvDistance.setText(String.format(getString(R.string.distance_text),"--"));
            }


            binding.btnAddPlaceToFavourite.setOnClickListener(v -> {
                if (binding.btnAddPlaceToFavourite.isChecked()){
                    addToFavourites();
                }else{
                    removeFromFavourites();
                }
            });

            isPlaceAFavourite();
            swipeListener = new SwipeListener(binding.infoWindowLayout);
            binding.btnHideWindow.setOnClickListener(v -> {
                if (getParentFragment() != null){
                    if (getParentFragment() instanceof MapsFragment){
                        ((MapsFragment) getParentFragment()).binding.placeInfoFragment.setVisibility(View.GONE);
                        ((MapsFragment) getParentFragment()).binding.btnNavigate.setVisibility(View.GONE);
                    }
                }
            });
        }
        return binding.getRoot();
    }

    /**
     * Determines if clicked place is in user's favourites and
     * if so sets checked property of checkbox to true
     */
    private void isPlaceAFavourite(){
        dbFavourites.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String placeID = dataSnapshot.getKey();
                        if (placeID!=null && placeID.equals(details.getPlaceId())) {
                            binding.btnAddPlaceToFavourite.setChecked(true);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Removes clicked place from user's favourites in Firebase
     */
    private void removeFromFavourites() {
        dbFavourites.child(userId).child(details.getPlaceId()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                showMessageErrorSnackBar(binding.infoWindowLayout,"Place successfully removed from Favourites",false);
            }else{
                showMessageErrorSnackBar(binding.infoWindowLayout, Objects.requireNonNull(task.getException()).getMessage(),true);
            }
        });
    }

    /**
     * Adds clicked item to user's favourites in Firebase
     */
    private void addToFavourites() {
        dbFavourites.child(userId).child(details.getPlaceId()).setValue(details.getPlaceId()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                showMessageErrorSnackBar(binding.infoWindowLayout, "Place successfully added to Favourites",false);
            }else{
                showMessageErrorSnackBar(binding.infoWindowLayout, "Attempt to Favourite this place unsuccessful",true);
            }
        });
    }

    private void onSwipeUp(){
        binding.infoWindowLayout.setVisibility(View.GONE);
    }

    private class SwipeListener implements View.OnTouchListener{
        GestureDetector gestureDetector;

        public SwipeListener(View view) {
            final int SWIPE_THRESHOLD = 100;
            final int VELOCITY_THRESHOLD = 100;
            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
                    try {
                        float diffX = moveEvent.getX()-downEvent.getX();
                        float diffY = moveEvent.getY()-downEvent.getY();
                        if (Math.abs(diffX)<Math.abs(diffY)){
                            //swipe bottom or top
                            if (Math.abs(diffY)>SWIPE_THRESHOLD && Math.abs(velocityY)>VELOCITY_THRESHOLD){
                                if (diffY>0){
                                    //upward swipe
                                    PlaceInfoWindowFragment.this.onSwipeUp();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return super.onFling(downEvent, moveEvent, velocityX, velocityY);
                }
            };
            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    }
}