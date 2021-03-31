package com.dashboard

import androidx.multidex.MultiDexApplication
import com.dashboard.rest.EndPoints.BOOST_KIT_NEW_BASE
import com.dashboard.rest.EndPoints.DEV_BOOST_KIT_URL
import com.dashboard.rest.EndPoints.PLUGIN_FLOATS_URL
import com.dashboard.rest.EndPoints.WEB_ACTION_API_BASE
import com.dashboard.rest.EndPoints.WEB_ACTION_KITSUNE_BASE
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
      PreferencesUtils.initSharedPreferences(application)
      BaseApplication.instance = application
      DevBoostKitApiClient.shared.init(DEV_BOOST_KIT_URL)
      DevBoostKitNewApiClient.shared.init(BOOST_KIT_NEW_BASE)
      WithFloatsApiClient.shared.init(WITH_FLOATS_BASE)
      PluginFloatsApiClient.shared.init(PLUGIN_FLOATS_URL)
      WebActionKitsuneClient.shared.init(WEB_ACTION_KITSUNE_BASE)
      WebActionApiBoostkitClient.shared.init(WEB_ACTION_API_BASE)
    }
  }
}