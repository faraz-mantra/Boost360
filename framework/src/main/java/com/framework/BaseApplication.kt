package com.framework

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.framework.firebaseUtils.firestore.restApi.DrScoreApiClient
import com.framework.firebaseUtils.firestore.restApi.EndPoints.DR_SCORE_BASE_BOOST
import com.framework.firebaseUtils.firestore.restApi.EndPoints.DR_SCORE_BASE_JIO
import com.framework.pref.APPLICATION_JIO_ID
import com.framework.rest.EndPoints.SALES_ASSIST_API
import com.framework.rest.errorTicketGenerate.SalesAssignErrorApiClient
import com.framework.utils.application

open class BaseApplication : MultiDexApplication() {

  val mFTActivityLifecycleCallbacks = FTActivityLifecycleCallbacks()

  override fun onCreate() {
    super.onCreate()
    instance = this
    registerActivityLifecycleCallbacks(mFTActivityLifecycleCallbacks)
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
  }

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      this.instance = application
      apiInitialize()
    }

    @JvmStatic
    fun apiInitialize() {
      DrScoreApiClient.shared.init(if(application().packageName.equals(APPLICATION_JIO_ID, ignoreCase = true)) DR_SCORE_BASE_JIO else DR_SCORE_BASE_BOOST)
      SalesAssignErrorApiClient.shared.init(SALES_ASSIST_API)
    }

    fun currentActivity(): AppCompatActivity? {
      return (instance as? BaseApplication)?.mFTActivityLifecycleCallbacks?.currentActivity as? AppCompatActivity
    }
    fun isInitialised() = ::instance.isInitialized
  }

  override fun attachBaseContext(base: Context) {
    MultiDex.install(this)
    super.attachBaseContext(base)
  }
}