package com.catlogservice

import androidx.multidex.MultiDexApplication
import com.catlogservice.rest.EndPoints
import com.catlogservice.rest.apiClients.RazorApiClient
import com.catlogservice.rest.apiClients.WithFloatsApiClient
import com.catlogservice.rest.apiClients.WithFloatsApiTwoClient
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class BaseCatalogApplication : BaseApplication() {

  val TAG = BaseCatalogApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      PreferencesUtils.initSharedPreferences(application)
      WithFloatsApiTwoClient.shared.init(EndPoints.WITH_FLOATS_TWO_BASE)
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE)
      RazorApiClient.shared.init(EndPoints.RAZOR_API_BASE)
      BaseApplication.instance = application
    }
  }
}