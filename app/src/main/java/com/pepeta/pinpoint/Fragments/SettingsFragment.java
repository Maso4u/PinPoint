package com.pepeta.pinpoint.Fragments;

import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pepeta.pinpoint.Constants;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.Settings;
import com.pepeta.pinpoint.databinding.FragmentSettingsBinding;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    FragmentSettingsBinding binding;

    private DatabaseReference dbSettings;

    //region DROP DOWN ADAPTER DECLERATIONS
    ArrayAdapter<String> preferredUnitsAdapterItems;
    ArrayAdapter<String> preferredLandMarksAdapterItems;
    ArrayAdapter<String> preferredRadiusesAdapterItems;
    ArrayAdapter<String> preferredTransportmodesAdapterItems;
    //endregion

    String[] measuringUnits, preferredLandMarks,preferredRadiuses, preferredTransportmodes;
    Settings settings;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_ID = "userID";

    // TODO: Rename and change types of parameters
    private String userID;

    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance(String userID) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userID = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(getLayoutInflater());
        if (!userID.isEmpty()){
            //region Database initialisation
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbReference = database.getReference();
            dbSettings = dbReference.child(Constants.NODE_SETTINGS);
            //endregion

            //region SET MEASURE UNIT SETTINGS DROPDOWN MENU LIST ITEMS
            measuringUnits = getResources().getStringArray(R.array.unit_types);
            preferredUnitsAdapterItems = new ArrayAdapter<>(getContext(),R.layout.settings_list_item,measuringUnits);
            binding.tvPreferredUnitTypeList.setAdapter(preferredUnitsAdapterItems);
            //endregion

            //region SET LANDMARK TYPE
            preferredLandMarks = getResources().getStringArray(R.array.preferred_landmark);
            preferredLandMarksAdapterItems = new ArrayAdapter<>(getContext(),R.layout.settings_list_item,preferredLandMarks);
            binding.tvPreferredLandMarkList.setAdapter(preferredLandMarksAdapterItems);
            //endregion

            //region SET RADIUSES
            preferredRadiuses = getResources().getStringArray(R.array.preferred_radius);
            preferredRadiusesAdapterItems = new ArrayAdapter<>(getContext(),R.layout.settings_list_item,preferredRadiuses);
            binding.tvPreferredSearchRadius.setAdapter(preferredRadiusesAdapterItems);
            //endregion

            //region SET TRANSPORTATION MODES
            preferredTransportmodes = getResources().getStringArray(R.array.preferred_transport_mode);
            preferredTransportmodesAdapterItems = new ArrayAdapter<>(getContext(),R.layout.settings_list_item,preferredTransportmodes);
            binding.tvPreferredMode.setAdapter(preferredTransportmodesAdapterItems);
            //endregion

            getUserSettings();
            binding.btnUpdateSettings.setOnClickListener(v -> {
                if(!binding.tvPreferredUnitTypeList.getText().toString().isEmpty() &&
                        !binding.tvPreferredLandMarkList.getText().toString().isEmpty() &&
                        !binding.tvPreferredMode.getText().toString().isEmpty() &&
                        !binding.tvPreferredSearchRadius.getText().toString().isEmpty()
                ){
                    settings.setPreferredLandMarkType(binding.tvPreferredLandMarkList.getText().toString());
                    settings.setPreferredMeasuringUnitType(binding.tvPreferredUnitTypeList.getText().toString());
                    settings.setRadius(binding.tvPreferredSearchRadius.getText().toString());
                    settings.setMode(binding.tvPreferredMode.getText().toString());
                    updateUserSettings();
                }
            });
        }

        //region turn keyboard input off
        binding.tvPreferredUnitTypeList.setShowSoftInputOnFocus(false);
        binding.tvPreferredUnitTypeList.setShowSoftInputOnFocus(false);
        binding.tvPreferredSearchRadius.setShowSoftInputOnFocus(false);
        binding.tvPreferredMode.setShowSoftInputOnFocus(false);
        //endregion

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    /**
     * get user's current settings in firebase.
     */
    private void getUserSettings() {
        settings = new Settings();
        dbSettings.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot settingsSnapshot:snapshot.getChildren()) {
                        if (Objects.equals(settingsSnapshot.getKey(), "preferredLandMarkType")) {
                            settings.setPreferredLandMarkType(Objects.requireNonNull(settingsSnapshot.getValue()).toString());
                        }
                        if (Objects.equals(settingsSnapshot.getKey(), "preferredMeasuringUnitType")) {
                            settings.setPreferredMeasuringUnitType(Objects.requireNonNull(settingsSnapshot.getValue()).toString());
                        }
                        if (settingsSnapshot.getKey().equals("mode")){
                            settings.setMode(Objects.requireNonNull(settingsSnapshot.getValue()).toString());
                        }
                        if (settingsSnapshot.getKey().equals("radius")){
                            settings.setRadius(Objects.requireNonNull(settingsSnapshot.getValue()).toString());
                        }
                    }
                    binding.tvPreferredUnitTypeList.setText(settings.getPreferredMeasuringUnitType(),false);
                    binding.tvPreferredLandMarkList.setText(settings.getPreferredLandMarkType(),false);
                    binding.tvPreferredMode.setText(settings.getMode(),false);
                    binding.tvPreferredSearchRadius.setText(settings.getRadius(),false);
                }else{

                    binding.tvPreferredUnitTypeList.setSelection(0);
                    binding.tvPreferredLandMarkList.setSelection(0);
                    binding.tvPreferredMode.setSelection(0);
                    binding.tvPreferredSearchRadius.setSelection(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * update user settings in firebase database
     */
    private void updateUserSettings(){
        dbSettings.child(userID).setValue(settings).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                showMessageErrorSnackBar(binding.settingLayout,"Settings updated successfully", false);
            }else showMessageErrorSnackBar(binding.settingLayout, Objects.requireNonNull(task.getException()).getMessage(), true);
        });
    }
}