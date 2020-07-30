package com.appservice

import androidx.multidex.MultiDexApplication
import com.appservice.rest.EndPoints
import com.appservice.rest.apiClients.RazorApiClient
import com.appservice.rest.apiClients.WebActionBoostKitApiClient
import com.appservice.rest.apiClients.WithFloatsApiClient
import com.appservice.rest.apiClients.WithFloatsApiTwoClient
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class AppServiceApplication : BaseApplication() {

  val TAG = AppServiceApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      PreferencesUtils.initSharedPreferences(application)
      WithFloatsApiTwoClient.shared.init(EndPoints.WITH_FLOATS_TWO_BASE)
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE)
      RazorApiClient.shared.init(EndPoints.RAZOR_API_BASE)
      WebActionBoostKitApiClient.shared.init(EndPoints.WEB_ACTION_BOOST_KIT_BASE)
      BaseApplication.instance = application
    }
  }
}