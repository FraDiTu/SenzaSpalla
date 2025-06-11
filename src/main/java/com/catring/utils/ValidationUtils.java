package com.catring.utils;

public class ValidationUtils {
    
    public static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static boolean isValidEmail(String email) {
        if (!isValidString(email)) {
            return false;
        }
        return email.contains("@") && email.contains(".");
    }
    
    public static boolean isValidId(String id) {
        return isValidString(id) && id.length() >= 3;
    }
    
    public static String sanitizeString(String value) {
        return value != null ? value.trim() : "";
    }
}