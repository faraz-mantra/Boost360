package com.boost.presignup.utils

import com.webengage.sdk.android.User
import com.webengage.sdk.android.WebEngage
import java.util.*

object WebEngageController {
    var weAnalytics = WebEngage.get().analytics()
    var weUser: User? = null


    fun trackEvent(event_name: String, event_label: String, event_value: String) {
        val trackEvent: MutableMap<String, Any> = HashMap()
        trackEvent["event_name"] = event_name
        trackEvent["fptag"] = event_value
        trackEvent["event_label"] = event_label
        weAnalytics.track(event_name, trackEvent)
    }


    fun logout() {
        weUser!!.logout()
    }
}