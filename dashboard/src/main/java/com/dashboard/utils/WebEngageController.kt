package com.dashboard.utils

import com.framework.analytics.NFWebEngageController

object WebEngageController {

    fun initiateUserLogin(userId: String?) = NFWebEngageController.initiateUserLogin(userId)

    fun setUserContactAttributes(email: String?, mobile: String?, name: String?, company: String?) =
            NFWebEngageController.setUserContactAttributes(email, mobile, name, company)

    fun trackEvent(event_name: String = "", event_label: String = "", event_value: String? = "") =
            NFWebEngageController.trackEvent(event_name, event_label, event_value ?: "")

    fun setFPTag(fpTag: String?) = NFWebEngageController.setFPTag(fpTag ?: "")

    fun logout() = NFWebEngageController.logout()
}