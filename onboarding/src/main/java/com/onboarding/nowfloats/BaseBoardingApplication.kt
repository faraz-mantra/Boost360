package com.onboarding.nowfloats

import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils
import com.nowfloats.twitter.TwitterConfigHelper
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.rest.EndPoints
import com.onboarding.nowfloats.rest.apiClients.*

open class BaseBoardingApplication : BaseApplication() {

  val TAG = BaseBoardingApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      BaseApplication.instance = application
      PreferencesUtils.initSharedPreferences(application)
      NfxApiClient.shared.init(EndPoints.NFX_BASE_URL)
      GMBApiClient.shared.init(EndPoints.GMB_BASE_URL, isAuthRemove = true)
      GoogleAuthApiClient.shared.init(EndPoints.GOOGLE_BASE_URL)
      WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE_URL)
      RiaWithFloatsApiClient.shared.init(EndPoints.RIA_WITH_FLOATS_BASE_URL)
      WebActionsApiClient.shared.init(EndPoints.WEB_ACTION_BASE_URL)
      BoostFloatClient.shared.init(EndPoints.BOOST_FLOATS_BASE_URL)
      TwitterConfigHelper.debug(true)
      TwitterConfigHelper.initialize(application)
      NavigatorManager.initialize()
    }
  }

}