package com.framework.analytics

import com.smartlook.sdk.smartlook.Smartlook

object SmartlookController {

    val YOUR_API_KEY="2976fda0fc620d3186eca19884bd55ac125b56e4"
    fun setupAndStartRecording(){
        Smartlook.setupAndStartRecording(YOUR_API_KEY);

    }
}