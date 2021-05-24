package com.dashboard

import androidx.multidex.MultiDexApplication
import com.dashboard.rest.EndPoints
import com.dashboard.rest.EndPoints.BOOST_KIT_NEW_BASE
import com.dashboard.rest.EndPoints.DEV_BOOST_KIT_URL
import com.dashboard.rest.EndPoints.NOW_FLOATS_BASE
import com.dashboard.rest.EndPoints.PLUGIN_FLOATS_URL
import com.dashboard.rest.EndPoints.WITH_FLOATS_BASE
import com.dashboard.rest.apiClients.*
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class AppDashboardApplication : BaseApplication() {

  val TAG = AppDashboardApplication::class.java.simpleName

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
      DevBoostKitApiClient.shared.init(DEV_BOOST_KIT_URL)
      DevBoostKitNewApiClient.shared.init(BOOST_KIT_NEW_BASE)
      WithFloatsApiClient.shared.init(WITH_FLOATS_BASE)
      WithFloatsTwoApiClient.shared.init(EndPoints.WITH_FLOATS_TWO_BASE)
      PluginFloatsApiClient.shared.init(PLUGIN_FLOATS_URL)
      NowFloatsApiClient.shared.init(NOW_FLOATS_BASE)
    }
  }
}