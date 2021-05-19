package com.inventoryorder

import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils
import com.inventoryorder.rest.EndPoints
import com.inventoryorder.rest.apiClients.*

open class BaseOrderApplication : BaseApplication() {

  val TAG = BaseOrderApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      BaseApplication.instance = application
      PreferencesUtils.initSharedPreferences(application)
      apiInitialize()
    }

    @JvmStatic
    fun apiInitialize() {
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE_URL)
      BoostFloatsApiClient.shared.init(EndPoints.BOOST_FLOATS_BASE_URL)
      Api2WithFloatClient.shared.init(EndPoints.BOOST_API2_WITH_FLOAT)
      ApiWithFloatClient.shared.init(EndPoints.BOOST_API_WITH_FLOAT)
      AssuredPurchaseClient.shared.init(EndPoints.ASSURED_PURCHASE_BASE_URL)
      WebActionBoostKitApiClient.shared.init(EndPoints.WEB_ACTION_BOOST_KIT_BASE_URL)
      NowFloatClient.shared.init(EndPoints.API_NOW_FLOATS)
    }
  }
}