package com.boost.upgrades.utils

import com.webengage.sdk.android.WebEngage
import java.util.*

object WebEngageController {
    var weAnalytics = WebEngage.get().analytics()

    fun trackEvent(event_name: String, event_label: String, event_value: String) {
        if(weAnalytics != null) {
            val trackEvent: MutableMap<String, Any> = HashMap()
            trackEvent["event_name"] = event_name
            trackEvent["event_value"] = event_value
            trackEvent["event_label"] = event_label
            weAnalytics.track(event_name, trackEvent)
        }
    }
}