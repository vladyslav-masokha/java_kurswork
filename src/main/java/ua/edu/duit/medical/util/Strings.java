package ua.edu.duit.medical.util;

public final class Strings {
    private Strings() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
}

