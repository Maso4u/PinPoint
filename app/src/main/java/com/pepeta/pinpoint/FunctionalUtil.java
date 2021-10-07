package com.pepeta.pinpoint;

import static androidx.core.content.ContextCompat.getColor;
import static androidx.core.content.ContextCompat.startActivity;

import static com.pepeta.pinpoint.R.color.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class FunctionalUtil {

    public static void styleSpan(TextView textView, Class<?> classTo){
        Context context =textView.getContext();
        SpannableString ss = new SpannableString(textView.getText());

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(context,classTo);
                context.startActivity(intent);
            }
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getColor(R.color.green));
                ds.setUnderlineText(false);
            }
        };

        if (RegisterActivity.class.equals(classTo)) {
            ss.setSpan(clickableSpan, 23, textView.getText().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (LoginActivity.class.equals(classTo)) {
            ss.setSpan(clickableSpan, 25, textView.getText().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void showMessageErrorSnackBar(View view, String message,boolean error){

        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();

        if (error){
            snackbarView.setBackgroundColor(getColor(view.getContext(), colorSnackBarError));
        }else {
            snackbarView.setBackgroundColor(getColor(view.getContext(), colorSnackBarSuccess));
        }
        snackbar.show();
    }

    /**
     * removes error set on given field
     * @param field - edit text with error to remove
     */
    public static void removeErrorMessage(TextInputEditText field) {
        field.setError(null);
    }

    /**
     * clear text in edit texts
     */
    public static void clearFields(TextInputEditText... editTexts){
        for (TextInputEditText editText: editTexts) editText.getText().clear();
    }

    /**
     * validates the password entered by the user
     * @param password - users potential password
     * @return - true if password given is valid and false if not
     */
    public static boolean validatePassword(String password, TextInputEditText etPassword){
        Context context = etPassword.getContext();
        if (password.isEmpty()){
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return false;
        } else removeErrorMessage(etPassword);

        if (!Constants.PASSWORD_DIGIT_PATTERN.matcher(password).find()){
            etPassword.setError(context.getString(R.string.password_numeric_error));
            etPassword.requestFocus();
            return false;
        } else removeErrorMessage(etPassword);

        if (!Constants.PASSWORD_LOWER_CASE_PATTERN.matcher(password).find()){
            etPassword.setError(context.getString(R.string.password_lowercase_error));
            etPassword.requestFocus();
            return false;
        } else removeErrorMessage(etPassword);

        if (!Constants.PASSWORD_UPPER_CASE_PATTERN.matcher(password).find()){
            etPassword.setError(context.getString(R.string.password_uppercase_error));
            etPassword.requestFocus();
            return false;
        } else removeErrorMessage(etPassword);

        if (!Constants.PASSWORD_SPECIAL_CHAR_PATTERN.matcher(password).find()){
            etPassword.setError(context.getString(R.string.password_special_char_error));
            etPassword.requestFocus();
            return false;
        } else removeErrorMessage(etPassword);

        if (!Constants.PASSWORD_LENGTH_PATTERN.matcher(password).matches()){
            etPassword.setError(context.getString(R.string.password_length_error));
            etPassword.requestFocus();
            return false;
        } else removeErrorMessage(etPassword);

        return true;
    }

}
