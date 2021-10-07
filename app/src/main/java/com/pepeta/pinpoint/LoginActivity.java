package com.pepeta.pinpoint;

import static com.pepeta.pinpoint.FunctionalUtil.removeErrorMessage;
import static com.pepeta.pinpoint.FunctionalUtil.showMessageErrorSnackBar;
import static com.pepeta.pinpoint.FunctionalUtil.styleSpan;
import static com.pepeta.pinpoint.FunctionalUtil.validatePassword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.fonts.FontFamily;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pepeta.pinpoint.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference dbReference;
    private DatabaseReference dbUsers;
    private Activity activity;
    User user = new User();

    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PinPoint);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        styleSpan(binding.tvRegisterPrompt,RegisterActivity.class);

        /*SpannableString ss = new SpannableString(getString(R.string.register_prompt));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(getBaseContext(), "weeeh", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getBaseContext(),RegisterActivity.class));
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.green));
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan,23, 31, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        ss.setSpan(new TypefaceSpan(String.valueOf(R.font.poppins_medium)),23,30,Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        binding.tvRegisterPrompt.setText(ss);
        binding.tvRegisterPrompt.setMovementMethod(LinkMovementMethod.getInstance());*/

        mAuth = FirebaseAuth.getInstance();
        binding.btnLogin.setOnClickListener(v->{
            database =FirebaseDatabase.getInstance();
            dbReference = database.getReference();
            dbUsers= dbReference.child(Constants.NODE_USERS);
            loginUser();
        });
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (validateFields(email,password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        user.setId(firebaseUser.getUid());
                        Intent intent = new Intent(binding.getRoot().getContext(),MainActivity.class);
                        startActivity(intent);
                    }else{
                        showMessageErrorSnackBar(binding.loginLayout,task.getException().getMessage(),true);
                    }
                }
            });
        }
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


}