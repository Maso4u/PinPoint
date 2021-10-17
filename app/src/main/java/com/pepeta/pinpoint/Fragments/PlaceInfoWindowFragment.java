package com.pepeta.pinpoint.Fragments;

import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pepeta.pinpoint.FunctionalUtil;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pepeta.pinpoint.Constants;
import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.databinding.FragmentPlaceInfoWindowBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceInfoWindowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceInfoWindowFragment extends Fragment {
    FragmentPlaceInfoWindowBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "title";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_WEBSITE = "website";
    private static final String ARG_CONTACT_NUMBER = "contactNumber";
    private static final String ARG_RATING = "rating";
    private static final String ARG_DETAILS = "details";
    private static final String ARG_USER_ID = "userID";


    // TODO: Rename and change types of parameters
    private String Name;
    private String Address;
    private String Website;
    private String Distance;
    private String Rating;
    private String contactNumber;
    private String userId;
    private DetailsModel details;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private DatabaseReference dbFavourites;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            details = getArguments().getParcelable(ARG_DETAILS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaceInfoWindowBinding.inflate(inflater,container,false);

        if (details!=null){
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            dbReference = database.getReference();
            dbFavourites= dbReference.child(Constants.NODE_FAVOURITES);

            binding.tvPlaceName.setText(details.getName());
            binding.tvPlaceAddress.setText(details.getFormattedAddress());
            binding.tvPlaceWebsiteUrl.setText(details.getWebsite());
            binding.tvRating.setText(String.format(getString(R.string.rating_text),details.getRating()));
            binding.tvPlaceWebsiteUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(details.getWebsite())));
                }
            });
            binding.tvContactNumber.setText(String.format(getString(R.string.contact_number_text),details.getFormattedPhoneNumber()));

            binding.btnAddPlaceToFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.btnAddPlaceToFavourite.isChecked()){
                        addToFavourites();
                    }else{
                        removeFromFavourites();
                    }
                }
            });

            isPlaceAFavourite();
        }
        return binding.getRoot();
    }
    private void isPlaceAFavourite(){
        dbFavourites.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        String placeID = dataSnapshot.getKey();
                        if (placeID.equals(details.getPlaceId())) {
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
    private void removeFromFavourites() {
        dbFavourites.child(userId).child(details.getPlaceId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showMessageErrorSnackBar(binding.infoWindowLayout,"Place successfully removed from Favourites",false);
                }else{
                    showMessageErrorSnackBar(binding.infoWindowLayout,task.getException().getMessage(),true);
                }
            }
        });
    }

    private void addToFavourites() {
        dbFavourites.child(userId).child(details.getPlaceId()).setValue(details.getPlaceId()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showMessageErrorSnackBar(binding.infoWindowLayout, "Place successfully added to Favourites",false);
                }else{
                    showMessageErrorSnackBar(binding.infoWindowLayout, "Attempt to Favourite this place unsuccessful",true);
                }
            }
        });
    }
}