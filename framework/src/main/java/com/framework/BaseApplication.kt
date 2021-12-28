package com.framework

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.framework.firebaseUtils.firestore.restApi.DrScoreApiClient
import com.framework.firebaseUtils.firestore.restApi.EndPoints.DR_SCORE_BASE
import com.framework.rest.EndPoints.SALES_ASSIST_API
import com.framework.rest.errorTicketGenerate.SalesAssignErrorApiClient

open class BaseApplication : MultiDexApplication() {

  override fun onCreate() {
    super.onCreate()
    instance = this
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
  }

  companion object {
    lateinit var instance: Application

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      this.instance = application
      apiInitialize()
    }

    @JvmStatic
    fun apiInitialize() {
      DrScoreApiClient.shared.init(DR_SCORE_BASE)
      SalesAssignErrorApiClient.shared.init(SALES_ASSIST_API)
    }
  }

  override fun attachBaseContext(base: Context) {
    MultiDex.install(this)
    super.attachBaseContext(base)
  }
}