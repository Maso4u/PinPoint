package com.pepeta.pinpoint;

import android.Manifest;

import java.util.regex.Pattern;

public class Constants {
    public static final String NODE_USERS = "Users";
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    public static final int LOCATION_PERMISSION_REQUEST_CODE =1234 ;
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSION_REQUEST_ENABLE_GPS = 9002;



    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    static final Pattern PASSWORD_DIGIT_PATTERN = Pattern.compile("\\d+");
    static final Pattern PASSWORD_LOWER_CASE_PATTERN= Pattern.compile("(?=.*[a-z])");
    static final Pattern PASSWORD_UPPER_CASE_PATTERN=Pattern.compile("(?=.*[A-Z])");
    static final Pattern PASSWORD_SPECIAL_CHAR_PATTERN=Pattern.compile("(?=.*[@#$%^&+=!-])");
    static final Pattern PASSWORD_NO_WHITESPACE_PATTERN=Pattern.compile("(?=\\S+$)");
    static final Pattern PASSWORD_LENGTH_PATTERN=Pattern.compile(".{6,}");


}
