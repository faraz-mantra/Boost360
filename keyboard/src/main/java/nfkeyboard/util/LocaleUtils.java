package nfkeyboard.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.annotation.StringDef;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

import com.android.inputmethod.keyboard.PointerTracker;
import com.android.inputmethod.keyboard.top.UpdateActionBarEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import io.separ.neural.inputmethod.indic.R;
import io.separ.neural.inputmethod.slash.EventBusExt;

public class LocaleUtils {
    private static int mLanguageIndex = 0;

    public static void handleConfigurationChange(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        InputMethodSubtype ims = imm.getCurrentInputMethodSubtype();

        String locale = ims.getLocale();
        if (locale.equalsIgnoreCase(LocaleUtils.ENGLISH)) {
            MixPanelUtils.getInstance().track(MixPanelUtils.SET_ENGLISH_KEYBOARD, null);
            mLanguageIndex = 0;
        } else {
            MixPanelUtils.getInstance().track(MixPanelUtils.SET_HINDI_KEYBOARD, null);
            PointerTracker.KEYBOARD_TYPED_KEY = null;
            mLanguageIndex = 1;
        }

        LocaleUtils.setLocale(context, mLanguageIndex);
        EventBusExt.getDefault().post(new UpdateActionBarEvent());
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ENGLISH, HINDI})
    public @interface LocaleDef {
        String[] SUPPORTED_LOCALES = {ENGLISH, HINDI};
    }

    private static String[] NORMAL_KEYS = {
            "क",
            "ख",
            "ग",
            "घ",
            "ङ",
            "च",
            "छ",
            "ज",
            "झ",
            "ञ",
            "ट",
            "ठ",
            "ड",
            "ढ",
            "ण",
            "त",
            "थ",
            "द",
            "ध",
            "न",
            "प",
            "फ",
            "ब",
            "भ",
            "म",
            "य",
            "र",
            "ल",
            "व",
            "ह",
            "श",
            "ष",
            "स",
            "ज्ञ",
            "क्ष",
            "श्र",
    };

    public static final String ENGLISH = "en_US";
    public static final String HINDI = "hi";

    public static void initialize(Context context) {
//        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        setLocale(context, ENGLISH);
    }

    public static void initialize(Context context, @LocaleDef String defaultLanguage) {
//        String lang = getPersistedData(context, defaultLanguage);
        setLocale(context, defaultLanguage);
    }

//    public static String getLanguage(Context context) {
//        return getPersistedData(context, Locale.getDefault().getLanguage());
//    }

    public static boolean setLocale(Context context, @LocaleDef String language) {
//        persist(context, language);
        return updateResources(context, language);
    }

    public static boolean setLocale(Context context, int languageIndex) {
//        persist(context, language);
        if (languageIndex >= LocaleDef.SUPPORTED_LOCALES.length) {
            return false;
        }

        return updateResources(context, LocaleDef.SUPPORTED_LOCALES[languageIndex]);
    }

//    private static String getPersistedData(Context context, String defaultLanguage) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
//    }

//    private static void persist(Context context, String language) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//
//        editor.putString(SELECTED_LANGUAGE, language);
//        editor.apply();
//    }

    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }

    public static boolean isNormalKeyLabel(Resources res, String label) {
        String[] normalKeys = res.getStringArray(R.array.normal_key);
        if (normalKeys.length == 0) {
            normalKeys = NORMAL_KEYS;
        }
        if (normalKeys != null) {
            for (int i = 0; i < normalKeys.length; i++) {
                if (label.equalsIgnoreCase(normalKeys[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}