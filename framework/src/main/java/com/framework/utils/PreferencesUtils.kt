package com.framework.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

enum class PreferencesKey {
  FACEBOOK_USER_TOKEN,
  FACEBOOK_USER_ID,
  REQUEST_FLOAT,
  NAVIGATION_STACK,
}

class PreferencesUtils {

  internal lateinit var sharedPref: SharedPreferences
  internal var editor: SharedPreferences.Editor? = null

  companion object {
    val instance = PreferencesUtils()
    fun initSharedPreferences(context: Context) {
      instance.sharedPref = context.getSharedPreferences(
        "prefs", Activity.MODE_PRIVATE
      )
      instance.editor = instance.sharedPref.edit()
    }
  }

  fun saveFacebookUserId(userId: String): Boolean {
    return saveData(PreferencesKey.FACEBOOK_USER_ID, userId)
  }

  fun getFacebookUserId(): String? {
    return getData(PreferencesKey.FACEBOOK_USER_ID)
  }

  fun saveFacebookUserToken(token: String): Boolean {
    return saveData(PreferencesKey.FACEBOOK_USER_TOKEN, token)
  }

  fun getFacebookUserToken(): String? {
    return getData(PreferencesKey.FACEBOOK_USER_TOKEN)
  }
}

@Synchronized
fun PreferencesUtils.saveData(key: String, value: String?): Boolean {
  editor?.putString(key, value)
  return editor?.commit() ?: false
}

@Synchronized
fun PreferencesUtils.saveData(key: PreferencesKey, value: String?): Boolean {
  editor?.putString(key.name, value)
  return editor?.commit() ?: false
}

@Synchronized
fun PreferencesUtils.saveData(key: String, value: Set<String>): Boolean {
  editor?.putStringSet(key, value)
  return editor?.commit() ?: false
}

@Synchronized
fun PreferencesUtils.saveData(key: String, value: Boolean): Boolean {
  editor?.putBoolean(key, value)
  return editor?.commit() ?: false
}

@Synchronized
fun PreferencesUtils.saveData(key: String, value: Long): Boolean {
  editor?.putLong(key, value)
  return editor?.commit() ?: false
}


@Synchronized
fun PreferencesUtils.saveData(key: String, value: Float): Boolean {
  editor?.putFloat(key, value)
  return editor?.commit() ?: false
}

@Synchronized
fun PreferencesUtils.saveData(key: String, value: Int): Boolean {
  editor?.putInt(key, value)
  return editor?.commit() ?: false
}

@Synchronized
fun PreferencesUtils.removeData(key: String): Boolean {
  editor?.remove(key)
  return editor?.commit() ?: false
}

@Synchronized
fun PreferencesUtils.getData(key: String, defaultValue: Boolean = false): Boolean {
  return sharedPref.getBoolean(key, defaultValue)
}

@Synchronized
fun PreferencesUtils.getData(key: String, defaultValue: String? = ""): String? {
  return sharedPref.getString(key, defaultValue)
}

@Synchronized
fun PreferencesUtils.getData(key: PreferencesKey, defaultValue: String? = ""): String? {
  return sharedPref.getString(key.name, defaultValue)
}

@Synchronized
fun PreferencesUtils.getData(key: String, defaultValue: Set<String>): Set<String>? {
  return sharedPref.getStringSet(key, defaultValue)
}

@Synchronized
fun PreferencesUtils.getData(key: String, defaultValue: Float): Float {

  return sharedPref.getFloat(key, defaultValue)
}

@Synchronized
fun PreferencesUtils.getData(key: String, defaultValue: Int = 0): Int {
  return sharedPref.getInt(key, defaultValue)
}

@Synchronized
fun PreferencesUtils.getData(key: String, defaultValue: Long): Long {
  return sharedPref.getLong(key, defaultValue)
}

@Synchronized
fun PreferencesUtils.deleteAllData() {
  editor?.clear()
  editor?.commit()
}