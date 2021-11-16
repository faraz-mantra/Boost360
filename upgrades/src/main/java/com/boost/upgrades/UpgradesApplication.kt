package com.boost.upgrades

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.framework.upgradeDB.local.AppDatabase
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class UpgradesApplication: BaseApplication() {
    companion object {
        lateinit var instance: MultiDexApplication

        @JvmStatic
        fun initModule(application: MultiDexApplication) {
            Log.e("UpgradesApplication", ">> UpgradesApplication")
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