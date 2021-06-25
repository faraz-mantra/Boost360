package com.framework

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

open class BaseApplication : MultiDexApplication() {

  override fun onCreate() {
    super.onCreate()
    instance = this
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
  }

  companion object {
    lateinit var instance: Application
    fun isInitialised() = ::instance.isInitialized
  }

  override fun attachBaseContext(base: Context) {
    MultiDex.install(this)
    super.attachBaseContext(base)
  }

}