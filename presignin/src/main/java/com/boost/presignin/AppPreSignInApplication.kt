package com.boost.presignin

import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class AppPreSignInApplication : BaseApplication() {

  val TAG = AppPreSignInApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication
    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      PreferencesUtils.initSharedPreferences(application)
      BaseApplication.instance = application
    }
  }
}