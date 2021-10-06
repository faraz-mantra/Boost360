package com.festive.poster

import androidx.multidex.MultiDexApplication
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGExternalFileResolver
import com.festive.poster.reset.EndPoints
import com.festive.poster.reset.apiClients.NowFloatsApiClient
import com.festive.poster.utils.SvgFileResolver

import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils

open class FestivePosterApplication : BaseApplication() {

  val TAG = FestivePosterApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      BaseApplication.initModule(application)
      PreferencesUtils.initSharedPreferences(application)
      SVG.registerExternalFileResolver(SvgFileResolver())
      apiInit()
    }

    private fun apiInit() {
      NowFloatsApiClient.shared.init(EndPoints.NOW_FLOATS_BASE)
    }

  }
}