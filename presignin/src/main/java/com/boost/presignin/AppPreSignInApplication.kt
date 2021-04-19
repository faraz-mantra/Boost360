package com.boost.presignin

import androidx.multidex.MultiDexApplication
import com.boost.presignin.rest.EndPoints
import com.boost.presignin.rest.apiClients.WithFloatsApiTwoClient
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class AppPreSignInApplication : BaseApplication() {

  val TAG = AppPreSignInApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication
    @JvmStatic
    fun initModule(application: MultiDexApplication) {
        PreferencesUtils.initSharedPreferences(application)
        BaseApplication.instance = application
        WithFloatsApiTwoClient.shared.init(EndPoints.WITH_FLOATS_TWO_BASE)

    }
  }
}