package com.appservice.utils

import com.appsflyer.AppsFlyerLib
import com.webengage.sdk.android.User
import com.webengage.sdk.android.WebEngage
import java.util.*

object WebEngageController {
    var weAnalytics = WebEngage.get().analytics()
    var weUser: User = WebEngage.get().user()
    var isUserLogedIn = false

    fun initiateUserLogin(userId: String?) {
        if (!userId.isNullOrEmpty()) {
            weUser.login(userId)
            isUserLogedIn = true
        }
    }

    fun setUserContactAttributes(email: String?, mobile: String?, name: String?) {
        if (isUserLogedIn) {
            if (!email.isNullOrEmpty()) {
                weUser.setEmail(email)
            }
            if (!mobile.isNullOrEmpty()) {
                weUser.setPhoneNumber(mobile)
            }
            if (!name.isNullOrEmpty()) {
                weUser.setFirstName(name)
            }
        }
    }

    fun setCategory(userCategory: String?) {
        try {
            if (!userCategory.isNullOrEmpty())
                weUser.setAttribute("Category", userCategory)
        } catch (e: Exception) {
        }
    }

    fun initiateUserLogout() {
        weUser.logout()
        isUserLogedIn = false
    }

    fun trackEvent(event_name: String, event_label: String, event_value: String) {
        val trackEvent: MutableMap<String, Any> = HashMap()
        trackEvent["event_name"] = event_name
        trackEvent["fptag/event_value"] = event_value
        trackEvent["event_label"] = event_label
        weAnalytics.track(event_name, trackEvent)

        //AppsFlyerEvent...
        AppsFlyerLib.getInstance().logEvent(weAnalytics.activity.get()?.applicationContext, event_name, trackEvent);

    }


    fun logout() {
        weUser!!.logout()
    }
}