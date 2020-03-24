package com.framework.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class PreferencesUtils {

  internal lateinit var sharedPref: SharedPreferences
  internal var editor: SharedPreferences.Editor? = null

  companion object {
    val instance = PreferencesUtils()
    fun initSharedPreferences(context: Context) {
      instance.sharedPref = context.getSharedPreferences("prefs", Activity.MODE_PRIVATE
      )
      instance.editor = instance.sharedPref.edit()
    }
  }

  @Synchronized
  fun saveData(key: String, value: String?): Boolean {
    editor?.putString(key, value)
    return editor?.commit() ?: false
  }

  @Synchronized
  fun saveData(key: String, value: Set<String>): Boolean {
    editor?.putStringSet(key, value)
    return editor?.commit() ?: false
  }

  @Synchronized
  fun saveData(key: String, value: Boolean): Boolean {
    editor?.putBoolean(key, value)
    return editor?.commit() ?: false
  }

  @Synchronized
  fun saveData(key: String, value: Long): Boolean {
    editor?.putLong(key, value)
    return editor?.commit() ?: false
  }


  @Synchronized
  fun saveData(key: String, value: Float): Boolean {
    editor?.putFloat(key, value)
    return editor?.commit() ?: false
  }

  @Synchronized
  fun saveData(key: String, value: Int): Boolean {
    editor?.putInt(key, value)
    return editor?.commit() ?: false
  }

  @Synchronized
  fun removeData(key: String): Boolean {
    editor?.remove(key)
    return editor?.commit() ?: false
  }

  @Synchronized
  fun getData(key: String, defaultValue: Boolean): Boolean {
    return sharedPref.getBoolean(key, defaultValue)
  }

  @Synchronized
  fun getData(key: String, defaultValue: String?): String? {
    return sharedPref.getString(key, defaultValue)
  }

  @Synchronized
  fun getData(key: String, defaultValue: Set<String>): Set<String>? {
    return sharedPref.getStringSet(key, defaultValue)
  }

  @Synchronized
  fun getData(key: String, defaultValue: Float): Float {

    return sharedPref.getFloat(key, defaultValue)
  }

  @Synchronized
  fun getData(key: String, defaultValue: Int): Int {
    return sharedPref.getInt(key, defaultValue)
  }

  @Synchronized
  fun getData(key: String, defaultValue: Long): Long {
    return sharedPref.getLong(key, defaultValue)
  }

  @Synchronized
  fun deleteAllData() {
    editor?.clear()
    editor?.commit()
  }
}