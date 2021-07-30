package com.framework.analytics

import com.smartlook.sdk.smartlook.Smartlook

object SmartlookController {

    val YOUR_API_KEY=""
    fun setupAndStartRecording(){
        Smartlook.setupAndStartRecording(YOUR_API_KEY);

    }
}