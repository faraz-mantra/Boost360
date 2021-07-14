package com.appservice.utils

import com.framework.analytics.NFWebEngageController

object WebEngageController {

  fun trackEvent(event_name: String, event_label: String, event_value: String?) =
    NFWebEngageController.trackEvent(event_name, event_label, event_value)

  fun logout() = NFWebEngageController.logout()
}