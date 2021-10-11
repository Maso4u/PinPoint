package com.pepeta.pinpoint.Activities;

import static com.pepeta.pinpoint.FunctionalUtil.removeErrorMessage;
import static com.pepeta.pinpoint.FunctionalUtil.clearFields;
import static com.pepeta.pinpoint.FunctionalUtil.styleSpan;
import static com.pepeta.pinpoint.FunctionalUtil.validatePassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pepeta.pinpoint.FunctionalUtil;
import com.pepeta.pinpoint.R;
import com.pepeta.pinpoint.User;
import com.pepeta.pinpoint.databinding.ActivityRegisterBinding;
import com.pepeta.pinpoint.Constants;

public class RegisterActivity extends AppCompatActivity{
    ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private DatabaseReference dbUsers;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PinPoint);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        styleSpan(binding.tvLoginPrompt,LoginActivity.class);

        mAuth = FirebaseAuth.getInstance();
        binding.btnRegister.setOnClickListener(v ->{
            database =FirebaseDatabase.getInstance();
            dbReference = database.getReference();
            dbUsers= dbReference.child(Constants.NODE_USERS);
            registerUser();
        } );
    }
    private void registerUser(){
        String email = binding.etEmail.getText().toString().trim();
        String fullName=binding.etFullname.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPW.getText().toString().trim();
        if (validateFields(email,fullName,password,confirmPassword)){
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        User user = new User(fullName,email,password, firebaseUser);

                        dbUsers.child(user.getId()).setValue(user).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task ) {
                                        if (task.isSuccessful()){
                                            clearFields(binding.etFullname, binding.etEmail,binding.etPassword,binding.etConfirmPW);
                                            FunctionalUtil.showMessageErrorSnackBar(binding.registerLayout,"Registeration successfull!",false);
                                        }else{
                                            //if registration unsuccessful
                                            FunctionalUtil.showMessageErrorSnackBar(binding.registerLayout,task.getException().getMessage(), true);
                                        }
                                    }
                                }
                        );
                        mAuth.signOut();
                    }else {
                        //if registration unsuccessful
                        FunctionalUtil.showMessageErrorSnackBar(binding.registerLayout,task.getException().getMessage(), true);
                        Log.d("REGERROR","Registeration Error"+task.getException().getMessage());
                    }
                }
            });
        }
    }


    /**
     * validates the password entered by the user
     * @param password - users potential password
     * @return - true if password given is valid and false if not
     */
    /*private boolean validatePassword(String password, TextInputEditText etPassword){
        if (password.isEmpty()){
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return false;
        }else{
            removeErrorMessage(binding.etPassword);
        }
        if (Constants.PASSWORD_DIGIT_PATTERN.matcher(password).matches()){
            etPassword.setError(getString(R.string.password_numeric_error));
            etPassword.requestFocus();
            return false;
        }else {
            removeErrorMessage(binding.etPassword);
        }
        if (Constants.PASSWORD_LOWER_CASE_PATTERN.matcher(password).matches()){
            etPassword.setError(getString(R.string.password_lowercase_error));
            etPassword.requestFocus();
            return false;
        }else {
            removeErrorMessage(binding.etPassword);
        }
        if (Constants.PASSWORD_UPPER_CASE_PATTERN.matcher(password).matches()){
            etPassword.setError(getString(R.string.password_uppercase_error));
            etPassword.requestFocus();
            return false;
        }else {
            removeErrorMessage(binding.etPassword);
        }
        if (Constants.PASSWORD_SPECIAL_CHAR_PATTERN.matcher(password).matches()){
            etPassword.setError(getString(R.string.password_special_char_error));
            etPassword.requestFocus();
            return false;
        }else {
            removeErrorMessage(binding.etPassword);
        }
        if (Constants.PASSWORD_LENGTH_PATTERN.matcher(password).matches()){
            etPassword.setError(getString(R.string.password_length_error));
            etPassword.requestFocus();
            return false;
        }else {
            removeErrorMessage(binding.etPassword);
        }
        return true;
    }
*/
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

}