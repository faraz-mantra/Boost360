package com.festive.poster.utils

import com.framework.analytics.NFWebEngageController
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.getDomainName

object WebEngageController {

  fun trackEvent(event_name: String= "", event_label: String= "", event_value: HashMap<String, Any>) =

    NFWebEngageController.trackEvent(event_name, event_label, event_value)

  fun trackEvent(event_name: String = "", event_label: String = "", event_value: String? = "") =
    NFWebEngageController.trackEvent(event_name, event_label, event_value ?: "")

  fun setFPTag(fpTag: String?) = NFWebEngageController.setFPTag(fpTag ?: "")

  fun logout() = NFWebEngageController.logout()
}