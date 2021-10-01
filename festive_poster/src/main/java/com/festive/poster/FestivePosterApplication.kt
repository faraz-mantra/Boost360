package com.festive.poster

import androidx.multidex.MultiDexApplication

import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class FestivePosterApplication : BaseApplication() {

  val TAG = FestivePosterApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      BaseApplication.initModule(application)
      PreferencesUtils.initSharedPreferences(application)
    }

  }
}