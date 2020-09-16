package com.boost.upgrades.utils

import com.webengage.sdk.android.WebEngage
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

object WebEngageController {
    var weAnalytics = WebEngage.get().analytics()

    fun trackEvent(event_name: String, event_label: String, event_value: String) {
        if(weAnalytics != null) {
            val event_attributes: HashMap<String, String> = HashMap()
            if(event_value != null && event_value.length > 0){
                event_attributes.put(event_label, event_value)
            }
            if(event_attributes.size > 0){
                weAnalytics.track(event_name, event_attributes)
            } else {
                weAnalytics.track(event_name)
            }

        }
    }

    fun trackEvent(event_name: String, event_label: String, event_value: Int) {
        if(weAnalytics != null) {
            val event_attributes: HashMap<String, Int> = HashMap()
            event_attributes.put(event_label, event_value)

            if(event_attributes.size > 0){
                weAnalytics.track(event_name, event_attributes)
            } else {
                weAnalytics.track(event_name)
            }

        }
    }

    fun trackEvent(event_name: String, event_label: String, event_value: HashMap<String, Any>) {
        if(weAnalytics != null) {
            val event_attributes: HashMap<String, HashMap<String, Any>> = HashMap()
            event_attributes.put(event_label, event_value)

            if(event_attributes.size > 0){
                weAnalytics.track(event_name, event_attributes)
            } else {
                weAnalytics.track(event_name)
            }

        }
    }
}