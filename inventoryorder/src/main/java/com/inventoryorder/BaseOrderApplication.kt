package com.inventoryorder

import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils
import com.inventoryorder.rest.EndPoints
import com.inventoryorder.rest.apiClients.BoostFloatsApiClient
import com.inventoryorder.rest.apiClients.WithFloatsApiClient

open class BaseOrderApplication : BaseApplication() {

  val TAG = BaseOrderApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      PreferencesUtils.initSharedPreferences(application)
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE_URL)
      BoostFloatsApiClient.shared.init(EndPoints.BOOST_FLOATS_BASE_URL)
      BaseApplication.instance = application
    }
  }
}