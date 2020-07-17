package com.catlogservice

import androidx.multidex.MultiDexApplication
import com.catlogservice.rest.EndPoints
import com.catlogservice.rest.apiClients.WithFloatsApiClient
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class BaseCatalogApplication : BaseApplication() {

  val TAG = BaseCatalogApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      PreferencesUtils.initSharedPreferences(application)
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE_URL)
      BaseApplication.instance = application
    }
  }
}