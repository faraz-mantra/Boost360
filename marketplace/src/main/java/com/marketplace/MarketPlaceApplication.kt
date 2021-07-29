package com.marketplace

import androidx.multidex.MultiDexApplication
import com.marketplace.rest.EndPoints.WITH_FLOATS_TWO_BASE
import com.marketplace.rest.apiClients.WithFloatsTwoApiClient
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