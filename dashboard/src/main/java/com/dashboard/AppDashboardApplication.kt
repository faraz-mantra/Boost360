package com.dashboard

import androidx.multidex.MultiDexApplication
import com.dashboard.rest.EndPoints.DEV_BOOST_KIT_URL
import com.dashboard.rest.EndPoints.WITH_FLOATS_BASE
import com.dashboard.rest.apiClients.DevBoostKitApiClient
import com.dashboard.rest.apiClients.WithFloatsApiClient
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class AppDashboardApplication : BaseApplication() {

  val TAG = AppDashboardApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication
    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      PreferencesUtils.initSharedPreferences(application)
      BaseApplication.instance = application
      DevBoostKitApiClient.shared.init(DEV_BOOST_KIT_URL)
      WithFloatsApiClient.shared.init(WITH_FLOATS_BASE)
    }
  }
}