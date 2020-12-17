package com.boost.presignup.utils

import com.smartlook.sdk.smartlook.Smartlook
import com.smartlook.sdk.smartlook.analytics.identify.UserProperties


object SmartLookController {

  @JvmStatic
  fun initiateSmartLook(apiKey: String) {
    Smartlook.setup(apiKey)
  }

  fun startRecording() {
    Smartlook.startRecording()
  }

  fun stopRecording() {
    Smartlook.stopRecording()
  }

  fun setUserAttributes(email: String?, mobile: String?, name: String?, clientId: String? = "") {
    val userProperties = UserProperties()
    if (!email.isNullOrEmpty()) {
      userProperties.putEmail(email)
    }
    if (!mobile.isNullOrEmpty()) {
      userProperties.put("phone_number", mobile)
    }
    if (!name.isNullOrEmpty()) {
      userProperties.putName(name)
    }
    if (!clientId.isNullOrEmpty()) {
      userProperties.put("clientId", clientId)
    }
    Smartlook.setUserProperties(userProperties)
  }

}