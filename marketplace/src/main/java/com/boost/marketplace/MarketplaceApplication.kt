package com.boost.marketplace

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class MarketplaceApplication: BaseApplication() {
    companion object {
        lateinit var instance: MultiDexApplication

        @JvmStatic
        fun initModule(application: MultiDexApplication) {
            Log.e("MarketplaceApplication", ">> MarketplaceApplication")
            BaseApplication.initModule(application)
            PreferencesUtils.initSharedPreferences(application)
        }
    }
}