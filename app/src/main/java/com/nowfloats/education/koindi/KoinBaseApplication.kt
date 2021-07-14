package com.nowfloats.education.koindi

import android.app.Application
import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class KoinBaseApplication : Application() {

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      instance = application
      startKoin {
        androidContext(instance)
        modules(listOf(koinModules, retrofitModule))
      }
    }
  }
}