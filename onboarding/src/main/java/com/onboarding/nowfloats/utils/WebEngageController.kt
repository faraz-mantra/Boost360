package com.onboarding.nowfloats.utils

import com.framework.analytics.NFWebEngageController

object WebEngageController {

    fun setCategory(userCategory: String?) = NFWebEngageController.setCategory(userCategory)

    fun trackEvent(event_name: String, event_label: String, event_value: String) =
            NFWebEngageController.trackEvent(event_name, event_label, event_value)

    fun logout() = NFWebEngageController.logout()
}