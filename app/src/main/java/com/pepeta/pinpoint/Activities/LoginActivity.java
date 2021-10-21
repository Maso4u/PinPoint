package com.pepeta.pinpoint.Activities;

import static android.content.ContentValues.TAG;
import static com.pepeta.pinpoint.Constants.COARSE_LOCATION;
import static com.pepeta.pinpoint.Constants.ERROR_DIALOG_REQUEST;
import static com.pepeta.pinpoint.Constants.FINE_LOCATION;
import static com.pepeta.pinpoint.Constants.LOCATION_PERMISSION_REQUEST_CODE;
import static com.pepeta.pinpoint.Constants.PERMISSION_REQUEST_ENABLE_GPS;
import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;
import static com.pepeta.pinpoint.FunctionalUtil.styleSpan;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.User;
import com.pepeta.pinpoint.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    User user = new User();
    private boolean mLocationPermissionGranted = false;

    ActivityResultLauncher<Intent> enableGpsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == PERMISSION_REQUEST_ENABLE_GPS){
                    if (mLocationPermissionGranted){
                        navigateToMainActivity();
                    }else getLocationPermission();
                }
            });

    private void navigateToMainActivity() {
        Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PinPoint);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        styleSpan(binding.tvRegisterPrompt,RegisterActivity.class);

        mAuth = FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener(v->loginUser());
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (user.getId()!=null){
            if(checkMapServices()){
                if (mLocationPermissionGranted){
                    navigateToMainActivity();
                }else getLocationPermission();
            }
        }
    }*/

    private void loginUser() {
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();

        if (validateFields(email,password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = task.getResult().getUser();

                    assert firebaseUser != null;
                    user.setId(firebaseUser.getUid());

                    if(checkMapServices()){
                        if (mLocationPermissionGranted){
                            navigateToMainActivity();
                        }else getLocationPermission();
                    }

                }else{
                    showMessageErrorSnackBar(binding.loginLayout,
                            Objects.requireNonNull(task.getException()).getMessage(),true);
                }
            });
        }
    }

    private boolean checkMapServices() {
        if (isServiceOK()) return isMapsEnabled();
        return false;
    }

    /**
     * validate whether values user entered in the edit texts are valid or not
     * @param email email address entered by user.
     * @param password  password entered by user.
     * @return true if all fields are valid and false if not.
     */
    private boolean validateFields(String email, String password) {
        if (email.isEmpty()){
            binding.etEmail.setError(getString(R.string.email_required_error));
            binding.etEmail.requestFocus();
            return false;
        }else if (password.isEmpty()) {
            binding.etPassword.setError(getString(R.string.password_required_error));
            binding.etPassword.requestFocus();
            return false;
        } else return true;
    }

    /**
     * Determine if user has google play services
     * @return whether the play service is okay.
     */
    public boolean isServiceOK(){
        Log.d(TAG, "isServiceOK: checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginActivity.this);
        if (available== ConnectionResult.SUCCESS){
            Log.d(TAG,"isServiceOK: Google Play Service working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginActivity.this,available, ERROR_DIALOG_REQUEST);
            assert dialog != null;
            dialog.show();
        }else{
            showMessageErrorSnackBar(binding.loginLayout,"You can't make map requests",true);
        }
        return false;
    }

    /**
     * Makes permission requests for the location of the device.
     * Result of the permission request is handled by onRequestPermissionResult
     */
    private void getLocationPermission(){
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted=true;
                navigateToMainActivity();
            }else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Determine whether GPS is enabled
     * @return whether google maps is enabled or not
     */
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    /**
     * builds alert to for Gps to be enabled
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent enableGpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    enableGpsLauncher.launch(enableGpsIntent);
//                        startActivityForResult(enableGpsIntent,PERMISSION_REQUEST_ENABLE_GPS);
                });
        final  AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted=false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        return;
                    }
                }
                mLocationPermissionGranted = true;
                //initial
            }
        }
    }
}