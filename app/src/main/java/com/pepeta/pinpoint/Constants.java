package com.pepeta.pinpoint;

import java.util.regex.Pattern;

public class Constants {
    public static final String NODE_USERS = "Users";

    static final Pattern PASSWORD_DIGIT_PATTERN = Pattern.compile("\\d+");
    static final Pattern PASSWORD_LOWER_CASE_PATTERN= Pattern.compile("(?=.*[a-z])");
    static final Pattern PASSWORD_UPPER_CASE_PATTERN=Pattern.compile("(?=.*[A-Z])");
    static final Pattern PASSWORD_SPECIAL_CHAR_PATTERN=Pattern.compile("(?=.*[@#$%^&+=!-])");
    static final Pattern PASSWORD_NO_WHITESPACE_PATTERN=Pattern.compile("(?=\\S+$)");
    static final Pattern PASSWORD_LENGTH_PATTERN=Pattern.compile(".{6,}");
}
