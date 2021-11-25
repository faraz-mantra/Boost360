package com.boost.presignin

import androidx.multidex.MultiDexApplication
import com.appservice.rest.apiClients.BoostKitDevApiClient
import com.boost.presignin.rest.EndPoints
import com.boost.presignin.rest.apiClients.*
import com.boost.presignin.rest.services.remote.BoostKitDevRemoteData
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
      RiaWithFloatsApiClient.shared.init(EndPoints.RIA_WITH_FLOATS_BASE)
      WithFloatsApiTwoClient.shared.init(EndPoints.WITH_FLOATS_TWO_BASE)
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE)
      com.boost.presignin.rest.apiClients.BoostKitDevApiClient.shared.init(EndPoints.BOOST_KIT_DEV_BASE)
      NfxFacebookAnalyticsClient.shared.init(EndPoints.NFX_WITH_NOWFLOATS)
      WebActionBoostKitClient.shared.init(EndPoints.WEB_ACTION_BOOST_KIT_API_URL)
    }
  }
}