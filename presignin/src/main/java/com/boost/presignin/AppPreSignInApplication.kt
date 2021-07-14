package com.boost.presignin

import androidx.multidex.MultiDexApplication
import com.boost.presignin.rest.EndPoints
import com.boost.presignin.rest.apiClients.NfxFacebookAnalyticsClient
import com.boost.presignin.rest.apiClients.WebActionBoostKitClient
import com.boost.presignin.rest.apiClients.WithFloatsApiClient
import com.boost.presignin.rest.apiClients.WithFloatsApiTwoClient
import com.boost.presignin.rest.services.remote.WithFloatsRemoteDataSource
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class AppPreSignInApplication : BaseApplication() {

  val TAG = AppPreSignInApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      BaseApplication.initModule(application)
      PreferencesUtils.initSharedPreferences(application)
      apiInitialize()
    }

    @JvmStatic
    fun apiInitialize() {
      WithFloatsApiTwoClient.shared.init(EndPoints.WITH_FLOATS_TWO_BASE)
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE)
      NfxFacebookAnalyticsClient.shared.init(EndPoints.NFX_WITH_NOWFLOATS)
      WebActionBoostKitClient.shared.init(EndPoints.WEB_ACTION_BOOST_KIT_API_URL)
    }
  }
}