package project_idea.idea.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LanguageUtils {
    private static final Set<String> SUPPORTED_LANGUAGES = new HashSet<>(Arrays.asList(
        "en", "es", "fr", "de", "it", "pt", "ru", "zh", "ja", "ko", "ar"
    ));

    public static boolean isValidLanguageCode(String languageCode) {
        if (languageCode == null || languageCode.length() != 2) {
            return false;
        }
        return SUPPORTED_LANGUAGES.contains(languageCode.toLowerCase());
    }

    public static String normalizeLanguageCode(String languageCode) {
        if (!isValidLanguageCode(languageCode)) {
            throw new IllegalArgumentException("Invalid language code: " + languageCode);
        }
        return languageCode.toLowerCase();
    }

    public static String getLanguageDisplayName(String languageCode) {
        if (!isValidLanguageCode(languageCode)) {
            throw new IllegalArgumentException("Invalid language code: " + languageCode);
        }
        return new Locale(languageCode).getDisplayLanguage(Locale.ENGLISH);
    }

    public static Set<String> getSupportedLanguages() {
        return new HashSet<>(SUPPORTED_LANGUAGES);
    }
}
