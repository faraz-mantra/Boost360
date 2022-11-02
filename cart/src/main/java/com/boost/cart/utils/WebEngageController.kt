package com.boost.cart.utils

import com.framework.analytics.NFWebEngageController

object WebEngageController {

  //Event with single attribute
  fun trackEvent(event_name: String, event_label: String, event_value: String) =
    NFWebEngageController.trackEvent(event_name, event_label, event_value)

  fun trackEvent(event_name: String, event_label: String, event_value: HashMap<String, Any>) =
    NFWebEngageController.trackEvent(event_name, event_label, event_value)

  fun trackEvent(
    event_name: String,
    event_label: String,
    event_value: HashMap<String, Any>,
    value: String
  ) =
    NFWebEngageController.trackEventLoad(event_name, event_label, event_value, value)
}