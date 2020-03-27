package com.onboarding.nowfloats

import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils
import com.nowfloats.twitter.TwitterConfigHelper
import com.onboarding.nowfloats.rest.ApiClient
import com.onboarding.nowfloats.rest.EndPoints

open class BaseBoardingApplication : BaseApplication() {

    val TAG = BaseBoardingApplication::class.java.simpleName

//    override fun onCreate() {
//        super.onCreate()
//        instance = this
//
//    }

    companion object {
        lateinit var instance: MultiDexApplication

        @JvmStatic
        public fun initModule(application: MultiDexApplication){
            PreferencesUtils.initSharedPreferences(application)
            ApiClient.shared.init(EndPoints.BASE_URL)
            TwitterConfigHelper.debug(true)
            TwitterConfigHelper.initialize(application)
        }
    }

}