package com.appservice

import androidx.multidex.MultiDexApplication
import com.appservice.rest.EndPoints
import com.appservice.rest.apiClients.*
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class AppServiceApplication : BaseApplication() {

  val TAG = AppServiceApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      BaseApplication.instance = application
      PreferencesUtils.initSharedPreferences(application)
      apiInitialize()
    }

    private fun apiInitialize() {
      WithFloatsApiTwoClient.shared.init(EndPoints.WITH_FLOATS_TWO_BASE)
      NowfloatsApiClient.shared.init(EndPoints.API_NOWFLOATS_COM_BASE)
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE)
      RazorApiClient.shared.init(EndPoints.RAZOR_API_BASE)
      BoostKitDevApiClient.shared.init(EndPoints.BOOST_KIT_DEV_BASE)
      WebActionBoostKitApiClient.shared.init(EndPoints.WEB_ACTION_BOOST_KIT_BASE)
      AssuredWithFloatsApiClient.shared.init(EndPoints.ASSURED_WITH_FLOATS_BASE_URL)
      StaffNowFloatsApiClient.shared.init(EndPoints.STAFF_BASE_URL)
      KitWebActionApiClient.shared.init(EndPoints.KIT_WEB_ACTION_WITH_FLOATS_BASE_URL)
    }
  }
}