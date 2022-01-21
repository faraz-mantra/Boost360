package com.framework.analytics

import com.framework.BuildConfig
import com.smartlook.sdk.smartlook.Smartlook

object SmartlookController {

  fun setupAndStartRecording() {
    Smartlook.setupAndStartRecording(BuildConfig.SMARTLOOK_API_KEY);

  }
}