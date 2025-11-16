package com.ridehub.route.helper;

import java.util.Locale;

public class TextUtils {

    public static String normalizeName(String input) {
        if (input == null)
            return null;

        // Make sure Vietnamese "đ" becomes "d"
        input = input.replace("đ", "d").replace("Đ", "D");

        // Remove accents
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Lowercase
        normalized = normalized.toLowerCase(Locale.ROOT);

        // Remove non-alphanumeric
        normalized = normalized.replaceAll("[^a-z0-9]", "");

        return normalized;
    }

}
