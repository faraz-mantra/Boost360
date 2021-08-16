package com.framework.utils

import android.content.Context
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

class AppsFlyerUtils : AppsFlyerConversionListener {

  companion object {
    private val TAG = AppsFlyerUtils::class.java.name

    @JvmField
    var sAttributionData: Map<String, String> = mapOf()

    @JvmStatic
    fun initAppsFlyer(context: Context?, devKey: String?) {
      AppsFlyerLib.getInstance().init(devKey!!, AppsFlyerUtils(), context!!)
      //Start the SDK
      AppsFlyerLib.getInstance().start(context)
      //Enable Debugging
      AppsFlyerLib.getInstance().setDebugLog(true)
    }

  }

  override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
    for (attrName in conversionData.keys) {
      Log.d(TAG, "attribute: " + attrName + " = " + conversionData[attrName])
    }
  }

  override fun onConversionDataFail(errorMessage: String) {
    Log.d(TAG, "error getting conversion data: $errorMessage")
  }

  override fun onAppOpenAttribution(attributionData: Map<String, String>) {
    sAttributionData = attributionData
    for (attrName in attributionData.keys) {
      Log.d(TAG, "attribute: " + attrName + " = " + attributionData[attrName])
    }
  }

  override fun onAttributionFailure(errorMessage: String) {
    Log.d(TAG, "error onAttributionFailure : $errorMessage")
  }
}