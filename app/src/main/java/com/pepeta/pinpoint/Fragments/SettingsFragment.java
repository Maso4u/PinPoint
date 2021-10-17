package com.pepeta.pinpoint.Fragments;

import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pepeta.pinpoint.Constants;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.Settings;
import com.pepeta.pinpoint.databinding.FragmentSettingsBinding;

import java.lang.reflect.Array;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    FragmentSettingsBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private DatabaseReference dbSettings;

    ArrayAdapter<String> preferredUnitsAdapterItems, preferredLandMarksAdapterItems;
    String[] measuringUnits, preferredLandMarks;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(getLayoutInflater());
        if (!userID.isEmpty()){

            //region Database initialisation
            mAuth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            dbReference = database.getReference();
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

            getUserSettings();
            binding.btnUpdateSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!binding.tvPreferredUnitTypeList.getText().toString().isEmpty() &&
                            !binding.tvPreferredLandMarkList.getText().toString().isEmpty()){
                        settings.setPreferredLandMarkType(binding.tvPreferredLandMarkList.getText().toString());
                        settings.setPreferredMeasuringUnitType(binding.tvPreferredUnitTypeList.getText().toString());
                        updateUserSettings();
                    }
                }
            });
        }
        binding.tvPreferredUnitTypeList.setShowSoftInputOnFocus(false);
        binding.tvPreferredUnitTypeList.setShowSoftInputOnFocus(false);

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
                        if (settingsSnapshot.getKey().equals("preferredLandMarkType")) {
                            settings.setPreferredLandMarkType(settingsSnapshot.getValue().toString());
                        }
                        if (settingsSnapshot.getKey().equals("preferredMeasuringUnitType")) {
                            settings.setPreferredMeasuringUnitType(settingsSnapshot.getValue().toString());
                        }
                    }
                    binding.tvPreferredUnitTypeList.setText(settings.getPreferredMeasuringUnitType(),false);
                    binding.tvPreferredLandMarkList.setText(settings.getPreferredLandMarkType(),false);
                }else{
                    binding.tvPreferredUnitTypeList.setSelection(0);
                    binding.tvPreferredLandMarkList.setSelection(0);
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
        dbSettings.child(userID).setValue(settings).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showMessageErrorSnackBar(binding.settingLayout,"Settings updated successfully", false);
                }else showMessageErrorSnackBar(binding.settingLayout,task.getException().getMessage(), true);
            }
        });
    }
}