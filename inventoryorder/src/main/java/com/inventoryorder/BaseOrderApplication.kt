package com.inventoryorder

import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class BaseOrderApplication : BaseApplication() {

  val TAG = BaseOrderApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      PreferencesUtils.initSharedPreferences(application)

      BaseApplication.instance = application
    }
  }

}