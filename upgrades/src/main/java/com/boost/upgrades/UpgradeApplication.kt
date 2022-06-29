package com.boost.upgrades

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class UpgradeApplication: BaseApplication() {
    companion object {
        lateinit var instance: MultiDexApplication

        @JvmStatic
        fun initModule(application: MultiDexApplication) {
            Log.e("UpgradeApplication", ">> UpgradeApplication")
//            MarketplaceNewApiClient.shared.init(DEVELOPER_BOOST_KIT_BASE_URL)
//            MarketplaceApiClient.shared.init(WITHFLOATS_BASE_URL)
            BaseApplication.initModule(application)

            PreferencesUtils.initSharedPreferences(application)

            AppDatabase.getInstance(application)

        }

        @JvmStatic
        fun clearDatabase(){
            Thread {
                AppDatabase.getInstance(instance)!!.clearAllTables()
            }.start()
        }
    }
}