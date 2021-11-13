package com.pepeta.pinpoint.Activities;

import static com.pepeta.pinpoint.Constants.PREFERRED_RADIUS;
import static com.pepeta.pinpoint.FunctionalUtil.removeErrorMessage;
import static com.pepeta.pinpoint.FunctionalUtil.clearFields;
import static com.pepeta.pinpoint.FunctionalUtil.styleSpan;
import static com.pepeta.pinpoint.FunctionalUtil.validatePassword;
import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;
import static com.pepeta.pinpoint.FunctionalUtil.hashPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pepeta.pinpoint.CustomDialog;
import com.pepeta.pinpoint.FunctionalUtil;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.Settings;
import com.pepeta.pinpoint.User;
import com.pepeta.pinpoint.databinding.ActivityRegisterBinding;
import com.pepeta.pinpoint.Constants;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity{
    ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private DatabaseReference dbUsers;
    private DatabaseReference dbSettings;
    CustomDialog customDialog;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PinPoint);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        styleSpan(binding.tvLoginPrompt,LoginActivity.class);
        customDialog = new CustomDialog(this);

        mAuth = FirebaseAuth.getInstance();
        binding.btnRegister.setOnClickListener(v ->{
            database =FirebaseDatabase.getInstance();
            dbReference = database.getReference();
            dbUsers= dbReference.child(Constants.NODE_USERS);
            dbSettings=dbReference.child(Constants.NODE_SETTINGS);
            registerUser();
        } );
    }
    private void registerUser(){
        //region credentials fields
        String email = Objects.requireNonNull(binding.etEmail.getText()).toString().trim();
        String fullName= Objects.requireNonNull(binding.etFullname.getText()).toString().trim();
        String password = Objects.requireNonNull(binding.etPassword.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(binding.etConfirmPW.getText()).toString().trim();
        //endregion
        if (validateFields(email,fullName,password,confirmPassword)){
            String finalPassword = hashPassword(password);
            customDialog.showDialog("attempting to create new account");
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            assert firebaseUser != null;
                            User user = new User(fullName,email, finalPassword, firebaseUser);
                            dbUsers.child(user.getId()).setValue(user).addOnCompleteListener(
                                    task1 -> {
                                        if (task1.isSuccessful()){
                                            clearFields(
                                                    binding.etFullname,
                                                    binding.etEmail,
                                                    binding.etPassword,
                                                    binding.etConfirmPW);
                                        }else{
                                            customDialog.dismissDialog();
                                            //if registration unsuccessful
                                            FunctionalUtil.showMessageErrorSnackBar(binding.registerLayout,
                                                    Objects.requireNonNull(task1.getException()).getMessage(), true);
                                        }
                                    }
                            );
                            dbSettings.child(user.getId()).setValue(createUserSettings()).addOnCompleteListener(settingsTask->{
                                if (settingsTask.isSuccessful()){
                                    customDialog.dismissDialog();
                                    FunctionalUtil.showMessageErrorSnackBar(binding.registerLayout,
                                            "Registeration successfull!",false);
                                }
                            });
                    mAuth.signOut();
                }else {
                    //if registration unsuccessful
                    customDialog.dismissDialog();
                    showMessageErrorSnackBar(binding.registerLayout,
                            Objects.requireNonNull(task.getException()).getMessage(), true);
                            Log.d("REGERROR","Registeration Error"+task.getException().getMessage());
                }
            });
        }
    }

    /**
     * validate whether values user entered in the edit texts are valid or not
     * @return - true if all fields are valid and false if not.
     */
    private boolean validateFields(String email,String fullName,String password,String confirmPassword){
        boolean valid = true;
        //validate fullname
        if (fullName.isEmpty()){
            binding.etFullname.setError("Full name is required.");
            binding.etFullname.requestFocus();
            valid=false;
        }else removeErrorMessage(binding.etFullname);
        //validate email address
        if (email.isEmpty()){
            binding.etEmail.setError(getString(R.string.email_required_error));
            binding.etEmail.requestFocus();
            valid=false;
        }else removeErrorMessage(binding.etEmail);

        //validate email address format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.setError("Please enter a valid email address.");
            binding.etEmail.requestFocus();
            valid =false;
        }else removeErrorMessage(binding.etEmail);

        //validate password
        if (validatePassword(password,binding.etPassword)){
            //validate confirm password
            if (confirmPassword.isEmpty()){
                binding.etConfirmPW.setError("Please confirm password");
                binding.etConfirmPW.requestFocus();
            }else removeErrorMessage(binding.etConfirmPW);
            //validate confirm password matches with initial password
            if (!confirmPassword.equals(password)){
                binding.etConfirmPW.setError("Passwords don't match.");
                binding.etConfirmPW.requestFocus();
            }else removeErrorMessage(binding.etConfirmPW);
        } else valid=false;

        return valid;
    }

    private Settings createUserSettings(){
        Settings settings = new Settings();
        settings.setPreferredMeasuringUnitType(getResources().getStringArray(R.array.unit_types)[0]);
        settings.setPreferredLandMarkType(getResources().getStringArray(R.array.preferred_landmark)[0]);
        settings.setRadius(PREFERRED_RADIUS[0]);
        settings.setMode(getResources().getStringArray(R.array.preferred_transport_mode)[0]);

        return settings;
        //boolean usersettting = dbSettings.child(userID).setValue(settings).isSuccessful();
    }
}