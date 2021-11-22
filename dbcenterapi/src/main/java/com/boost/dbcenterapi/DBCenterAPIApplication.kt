package com.boost.dbcenterapi

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.utils.DataLoader
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class DBCenterAPIApplication: BaseApplication() {
    companion object {
        lateinit var instance: MultiDexApplication

        @JvmStatic
        fun initModule(application: MultiDexApplication) {
            Log.e("DBCenterAPIApplication", ">> DBCenterAPIApplication")
            BaseApplication.initModule(application)
            PreferencesUtils.initSharedPreferences(application)
            AppDatabase.getInstance(application)
            DataLoader.loadMarketPlaceData(application,"","")
        }

        @JvmStatic
        fun clearDatabase(){
            Thread {
                AppDatabase.getInstance(instance)!!.clearAllTables()
            }.start()
        }
    }
}