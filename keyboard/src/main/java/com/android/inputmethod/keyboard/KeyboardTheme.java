/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.keyboard;

import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;

import java.util.Arrays;

import io.separ.neural.inputmethod.annotations.UsedForTesting;
import io.separ.neural.inputmethod.compat.BuildCompatUtils;
import io.separ.neural.inputmethod.indic.R;

public final class KeyboardTheme implements Comparable<KeyboardTheme> {
    // These should be aligned with Keyboard.themeId and Keyboard.Case.keyboardTheme
    // attributes' values in attrs.xml.
    public static final int THEME_ID_KLP = 1;
    public static final int THEME_ID_LXX_LIGHT = 2;
    public static final int THEME_ID_LXX_DARK = 3;
    public static final int THEME_ID_LXX_DARK_UNBORDERED = 4;
    public static final int DEFAULT_THEME_ID = THEME_ID_LXX_DARK;
    static final String KLP_KEYBOARD_THEME_KEY = "pref_keyboard_layout_20110916";
    static final String LXX_KEYBOARD_THEME_KEY = "pref_keyboard_theme_20140509";
    private static final String TAG = KeyboardTheme.class.getSimpleName();
    private static final KeyboardTheme[] KEYBOARD_THEMES = {
            new KeyboardTheme(THEME_ID_KLP, "KLP", R.style.KeyboardTheme_KLP,
                    // Default theme for ICS, JB, and KLP.
                    VERSION_CODES.ICE_CREAM_SANDWICH),
            new KeyboardTheme(THEME_ID_LXX_LIGHT, "LXXLight", R.style.KeyboardTheme_LXX_Dark,
                    // Default theme for LXX.
                    BuildCompatUtils.VERSION_CODES_LXX),
            new KeyboardTheme(THEME_ID_LXX_DARK, "LXX_DARK", R.style.KeyboardTheme_LXX_Dark,
                    VERSION_CODES.BASE),
            new KeyboardTheme(THEME_ID_LXX_DARK_UNBORDERED, "LXX_DARK_UNBORDERED", R.style.KeyboardTheme_LXX_Dark_Unbordered,
                    VERSION_CODES.BASE),
    };

    static {
        // Sort {@link #KEYBOARD_THEME} by descending order of {@link #mMinApiVersion}.
        Arrays.sort(KEYBOARD_THEMES);
    }

    public final int mThemeId;
    public final int mStyleId;
    public final String mThemeName;
    private final int mMinApiVersion;

    // Note: The themeId should be aligned with "themeId" attribute of Keyboard style
    // in values/themes-<style>.xml.
    private KeyboardTheme(final int themeId, final String themeName, final int styleId,
                          final int minApiVersion) {
        mThemeId = themeId;
        mThemeName = themeName;
        mStyleId = styleId;
        mMinApiVersion = minApiVersion;
    }

    @UsedForTesting
    static KeyboardTheme searchKeyboardThemeById(final int themeId) {
        // TODO: This search algorithm isn't optimal if there are many themes.
        for (final KeyboardTheme theme : KEYBOARD_THEMES) {
            if (theme.mThemeId == themeId) {
                return theme;
            }
        }
        return null;
    }

    @UsedForTesting
    static KeyboardTheme getDefaultKeyboardTheme(final SharedPreferences prefs,
                                                 final int sdkVersion) {
        return searchKeyboardThemeById(DEFAULT_THEME_ID);
    }

    public static String getKeyboardThemeName(final int themeId) {
        final KeyboardTheme theme = searchKeyboardThemeById(themeId);
        return theme.mThemeName;
    }

    public static void saveKeyboardThemeId(final String themeIdString,
                                           final SharedPreferences prefs) {
        saveKeyboardThemeId(themeIdString, prefs, BuildCompatUtils.EFFECTIVE_SDK_INT);
    }

    @UsedForTesting
    static String getPreferenceKey(final int sdkVersion) {
        if (sdkVersion <= VERSION_CODES.KITKAT) {
            return KLP_KEYBOARD_THEME_KEY;
        }
        return LXX_KEYBOARD_THEME_KEY;
    }

    @UsedForTesting
    static void saveKeyboardThemeId(final String themeIdString,
                                    final SharedPreferences prefs, final int sdkVersion) {
        final String prefKey = getPreferenceKey(sdkVersion);
        prefs.edit().putString(prefKey, themeIdString).apply();
    }

    public static KeyboardTheme getKeyboardTheme(final SharedPreferences prefs) {
        return getKeyboardTheme(prefs, BuildCompatUtils.EFFECTIVE_SDK_INT);
    }

    @UsedForTesting
    static KeyboardTheme getKeyboardTheme(final SharedPreferences prefs, final int sdkVersion) {
        //final String lxxThemeIdString = prefs.getString(LXX_KEYBOARD_THEME_KEY, null);
        final String themeId = prefs.getString("keyboard_theme", "LXX_DARK");
        /*if (lxxThemeIdString == null) {
            return getDefaultKeyboardTheme(prefs, sdkVersion);
        }
        try {
            final int themeId = Integer.parseInt(lxxThemeIdString);
            final KeyboardTheme theme = searchKeyboardThemeById(themeId);
            if (theme != null) {
                return theme;
            }
            Log.w(TAG, "Unknown keyboard theme in LXX preference: " + lxxThemeIdString);
        } catch (final NumberFormatException e) {
            Log.w(TAG, "Illegal keyboard theme in LXX preference: " + lxxThemeIdString, e);
        }
        // Remove preference that contains unknown or illegal theme id.
        prefs.edit().remove(LXX_KEYBOARD_THEME_KEY).apply();*/
        if (android.os.Build.VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP) {
            return searchKeyboardThemeById(THEME_ID_KLP);
        } else {
            if (themeId.equals(getKeyboardThemeName(THEME_ID_LXX_DARK))) {
                return getDefaultKeyboardTheme(prefs, sdkVersion);
            } else if (themeId.equals(getKeyboardThemeName(THEME_ID_LXX_DARK_UNBORDERED))) {
                return searchKeyboardThemeById(THEME_ID_LXX_DARK_UNBORDERED);
            } else {
                return getDefaultKeyboardTheme(prefs, sdkVersion);
            }
        }
    }

    @Override
    public int compareTo(@NonNull final KeyboardTheme rhs) {
        if (mMinApiVersion > rhs.mMinApiVersion) return -1;
        if (mMinApiVersion < rhs.mMinApiVersion) return 1;
        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof KeyboardTheme) && ((KeyboardTheme) o).mThemeId == mThemeId;
    }

    @Override
    public int hashCode() {
        return mThemeId;
    }
}
