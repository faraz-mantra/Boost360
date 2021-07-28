package com.addons

import androidx.multidex.MultiDexApplication
import com.addons.rest.EndPoints.WITH_FLOATS_TWO_BASE
import com.addons.rest.apiClients.WithFloatsTwoApiClient
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class MarketPlaceApplication : BaseApplication() {

  val TAG = MarketPlaceApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      BaseApplication.initModule(application)
      PreferencesUtils.initSharedPreferences(application)
      apiInitialize()
    }

    @JvmStatic
    fun apiInitialize() {
      WithFloatsTwoApiClient.shared.init(WITH_FLOATS_TWO_BASE)

    }
  }
}