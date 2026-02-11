package com.mycompany.library_system.utility;

public class InputValidator {
    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
