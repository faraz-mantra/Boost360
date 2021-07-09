package com.boost.presignup.locale

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
import java.util.*

class LocaleManager {

    val LANGUAGE_ENGLISH = "en"
    private val LANGUAGE_KEY = "language_key"

    private var prefs: SharedPreferences? = null

    constructor(context: Context?) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun setLocale(c: Context): Context? {
        return updateResources(c, getLanguage())
    }

    fun setNewLocale(
        c: Context,
        language: String
    ): Context? {
        persistLanguage(language)
        return updateResources(c, language)
    }

    fun getLanguage(): String? {
        return prefs!!.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH)
    }

    @SuppressLint("ApplySharedPref")
    private fun persistLanguage(language: String) { // use commit() instead of apply(), because sometimes we kill the application process immediately
// which will prevent apply() to finish
        prefs!!.edit().putString(LANGUAGE_KEY, language).commit()
    }

    private fun updateResources(
        context: Context,
        language: String?
    ): Context? {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config =
            Configuration(res.configuration)
        if (isAtLeastVersion(VERSION_CODES.JELLY_BEAN_MR1)) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    fun getLocale(res: Resources): Locale? {
        val config = res.configuration
        return if (isAtLeastVersion(VERSION_CODES.N)) config.locales[0] else config.locale
    }

    private fun isAtLeastVersion(version: Int): Boolean {
        return Build.VERSION.SDK_INT >= version
    }
}