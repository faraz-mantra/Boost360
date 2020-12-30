package com.boost.upgrades.utils

import com.framework.analytics.FirebaseAnalyticsUtils
import com.framework.analytics.FirebaseEvent
import com.webengage.sdk.android.WebEngage

object WebEngageController {
    var weAnalytics = WebEngage.get().analytics()

    //Event with single attribute
    fun trackEvent(event_name: String, event_label: String, event_value: String) {
        if (weAnalytics != null) {
            val event_attributes: HashMap<String, String> = HashMap()
            if (event_value != null && event_value.length > 0) {
                event_attributes.put(event_label, event_value)
            }
            if (event_attributes.size > 0) {
                weAnalytics.track(event_name, event_attributes)

                //Firebase Analytics Event...
                val event = FirebaseEvent(event_name)
                event.putMap(event_attributes)
                FirebaseAnalyticsUtils.logEvent(event.getName(), event.getBundle())
            } else {
                weAnalytics.track(event_name)

                //Firebase Analytics Event...
                FirebaseAnalyticsUtils.logEvent(event_name, "", "")
            }

        }
    }

    //Event with multiple attribute
    fun trackEvent(event_name: String, event_attributes: HashMap<String, Double>) {
        if (weAnalytics != null) {
            if (event_attributes.size > 0) {
                weAnalytics.track(event_name, event_attributes)

                //Firebase Analytics Event...
                val event = FirebaseEvent(event_name)
                event.putDoubleMap(event_attributes)
                FirebaseAnalyticsUtils.logEvent(event.getName(), event.getBundle())
            } else {
                weAnalytics.track(event_name)

                //Firebase Analytics Event...
                FirebaseAnalyticsUtils.logEvent(event_name, "", "")
            }

        }
    }

    //Event with single integer attribute
    fun trackEvent(event_name: String, event_label: String, event_value: Int) {
        if (weAnalytics != null) {
            val event_attributes: HashMap<String, Int> = HashMap()
            event_attributes.put(event_label, event_value)

            if (event_attributes.size > 0) {
                weAnalytics.track(event_name, event_attributes)

                //Firebase Analytics Event...
                val event = FirebaseEvent(event_name)
                event.putIntMap(event_attributes)
                FirebaseAnalyticsUtils.logEvent(event.getName(), event.getBundle())
            } else {
                weAnalytics.track(event_name)

                //Firebase Analytics Event...
                FirebaseAnalyticsUtils.logEvent(event_name, "", "")
            }

        }
    }

    fun trackEvent(event_name: String, event_label: String, event_value: HashMap<String, Any>) {
        if (weAnalytics != null) {
            val event_attributes: HashMap<String, HashMap<String, Any>> = HashMap()
            event_attributes.put(event_label, event_value)

            if (event_attributes.size > 0) {
                weAnalytics.track(event_name, event_attributes)
            } else {
                weAnalytics.track(event_name)
            }

        }
    }
}