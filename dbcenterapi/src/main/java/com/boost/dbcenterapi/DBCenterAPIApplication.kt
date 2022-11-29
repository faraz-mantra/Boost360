package com.boost.dbcenterapi

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.boost.dbcenterapi.data.rest.EndPoints.DEVELOPER_BOOST_KIT_BASE_URL
import com.boost.dbcenterapi.data.rest.EndPoints.WITHFLOATS_BASE_URL
import com.boost.dbcenterapi.data.rest.apiClients.MarketplaceApiClient
import com.boost.dbcenterapi.data.rest.apiClients.MarketplaceNewApiClient
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class DBCenterAPIApplication: BaseApplication() {
    companion object {
        lateinit var instance: MultiDexApplication

        @JvmStatic
        fun initModule(application: MultiDexApplication) {
            Log.e("DBCenterAPIApplication", ">> DBCenterAPIApplication")
            MarketplaceNewApiClient.shared.init(DEVELOPER_BOOST_KIT_BASE_URL)
            MarketplaceApiClient.shared.init(WITHFLOATS_BASE_URL)
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