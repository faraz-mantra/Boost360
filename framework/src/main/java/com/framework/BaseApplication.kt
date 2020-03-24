package com.framework

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
    lateinit var instance: BaseApplication
  }

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

}