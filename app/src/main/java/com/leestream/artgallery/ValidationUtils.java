package com.leestream.artgallery;

import java.util.regex.Pattern;

public class ValidationUtils {
    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }

    public static boolean isValidEmail(String email) {
        // email validation logic
        return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)" +
                "*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // Password should have at least 6 characters
        return password.length() >= 6;
    }
}
