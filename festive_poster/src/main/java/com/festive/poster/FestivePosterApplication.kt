package com.festive.poster

import androidx.multidex.MultiDexApplication
import com.caverock.androidsvg.SVG
import com.festive.poster.reset.EndPoints
import com.festive.poster.reset.apiClients.*
import com.framework.glide.customsvgloader.FileUtils
import com.festive.poster.utils.SvgFileResolver

import com.framework.BaseApplication
import com.framework.utils.PreferencesUtils
import java.io.File

open class FestivePosterApplication : BaseApplication() {

  val TAG = FestivePosterApplication::class.java.simpleName

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      BaseApplication.initModule(application)
      PreferencesUtils.initSharedPreferences(application)
      SVG.registerExternalFileResolver(SvgFileResolver())
      if (!File(FileUtils.getPathOfImages(instance)).exists()){
        File(FileUtils.getPathOfImages(context = instance)).mkdirs()
      }
      apiInit()
    }

    private fun apiInit() {
      UsCentralNowFloatsCloudApiClient.shared.init(EndPoints.US_CENTRAL_BASE)
      WithFloatsTwoApiClient.shared.init(EndPoints.WITH_FLOATS_TWO_BASE)
      NowFloatsApiClient.shared.init(EndPoints.NOW_FLOATS_BASE)
      FeatureProcessorApiClient.shared.init(EndPoints.FEATURE_PROCESSOR_BASE)
      DevBoostKitApiClient.shared.init(EndPoints.BOOST_KIT_DEV_BASE)
    }

  }
}