package com.onboarding.nowfloats

import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils
import com.nowfloats.twitter.TwitterConfigHelper
import com.onboarding.nowfloats.managers.NavigatorManager
import com.onboarding.nowfloats.rest.apiClients.NfxApiClient
import com.onboarding.nowfloats.rest.EndPoints
import com.onboarding.nowfloats.rest.apiClients.WebActionsApiClient
import com.onboarding.nowfloats.rest.apiClients.WithFloatsApiClient

open class BaseBoardingApplication : BaseApplication() {

    val TAG = BaseBoardingApplication::class.java.simpleName

    companion object {
        lateinit var instance: MultiDexApplication

        @JvmStatic
        public fun initModule(application: MultiDexApplication){
            PreferencesUtils.initSharedPreferences(application)
            NfxApiClient.shared.init(EndPoints.NFX_BASE_URL)
            WithFloatsApiClient.shared.init(EndPoints.WITH_FLOATS_BASE_URL)
            WebActionsApiClient.shared.init(EndPoints.WEB_ACTION_BASE_URL)
            TwitterConfigHelper.debug(true)
            TwitterConfigHelper.initialize(application)
            NavigatorManager.initialize()
        }
    }

}