package com.pepeta.pinpoint.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pepeta.pinpoint.Model.PlaceDetails.DetailsModel;
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


    // TODO: Rename and change types of parameters
    private String Name;
    private String Address;
    private String Website;
    private String Distance;
    private String Rating;
    private String contactNumber;
    private DetailsModel details;

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
    public static PlaceInfoWindowFragment newInstance(DetailsModel detailsModel) {
        PlaceInfoWindowFragment fragment = new PlaceInfoWindowFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DETAILS,detailsModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            details = getArguments().getParcelable(ARG_DETAILS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaceInfoWindowBinding.inflate(inflater,container,false);

        if (details!=null){
            binding.tvPlaceName.setText(details.getName());
            binding.tvPlaceAddress.setText(details.getFormattedAddress());
            binding.tvPlaceWebsiteUrl.setText(details.getWebsite());
            binding.tvRating.setText(details.getRating().toString());
            binding.tvPlaceWebsiteUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(details.getWebsite())));
                }
            });
            binding.tvContactNumber.setText(details.getFormattedPhoneNumber());
        }
        return binding.getRoot();
    }
}