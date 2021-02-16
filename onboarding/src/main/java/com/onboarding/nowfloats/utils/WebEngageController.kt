package com.onboarding.nowfloats.utils

import com.appsflyer.AppsFlyerLib
import com.framework.analytics.FirebaseAnalyticsUtilsHelper
import com.webengage.sdk.android.User
import com.webengage.sdk.android.WebEngage

object WebEngageController {
    var weAnalytics = WebEngage.get().analytics()
    var weUser: User = WebEngage.get().user()
    var isUserLogedIn = false

    fun initiateUserLogin(userId: String?) {
        if (!userId.isNullOrEmpty()) {
            weUser.login(userId)

            //Firebase Analytics User Session Event.
            FirebaseAnalyticsUtilsHelper.identifyUser(userId)

            //AppsFlyer Analytics User Session Event.
            AppsFlyerLib.getInstance().logSession(weAnalytics.activity.get()?.application)
            AppsFlyerLib.getInstance().setCustomerUserId(userId)

            isUserLogedIn = true
        }
    }

    fun setUserContactAttributes(email: String?, mobile: String?, name: String?) {
        if (isUserLogedIn) {
            if (!email.isNullOrEmpty()) {
                weUser.setEmail(email)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("emailId", email)
            }
            if (!mobile.isNullOrEmpty()) {
                weUser.setPhoneNumber(mobile)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("mobile", mobile)
            }
            if (!name.isNullOrEmpty()) {
                weUser.setFirstName(name)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("name", name)
            }
        }
    }

    fun setCategory(userCategory: String?) {
        try {
            if (!userCategory.isNullOrEmpty()) {
                weUser.setAttribute("Category", userCategory)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("Category", userCategory)

                //AppsFlyer User Property
                val params = HashMap<String, Any>()
                params["Category"] = userCategory
                AppsFlyerLib.getInstance().setAdditionalData(params)

            }

        } catch (e: Exception) {
        }
    }

    fun initiateUserLogout() {
        weUser.logout()

        //Reset Firebase Analytics User Session Event.
        FirebaseAnalyticsUtilsHelper.resetIdentifyUser()

        isUserLogedIn = false
    }

    fun trackEvent(event_name: String, event_label: String, event_value: String) {
        val trackEvent: MutableMap<String, String> = HashMap()
        trackEvent["event_name"] = event_name
        trackEvent["fptag/event_value"] = event_value
        trackEvent["event_label"] = event_label
        weAnalytics.track(event_name, trackEvent)
        weAnalytics.screenNavigated(event_name);
        //Firebase Analytics Event...
        FirebaseAnalyticsUtilsHelper.logDefinedEvent(event_name, event_label, event_value)

        //AppsFlyerEvent...
        try {
            AppsFlyerLib.getInstance().logEvent(weAnalytics.activity.get()?.applicationContext, event_name, trackEvent.toMap());
        } catch (e: Exception) {
        }

    }


    fun logout() {
        weUser!!.logout()

        //Reset Firebase Analytics User Session Event.
        FirebaseAnalyticsUtilsHelper.resetIdentifyUser()

        //End AppsFlyer Analytics User Session Event.
        AppsFlyerLib.getInstance().setCustomerUserId(null)

    }
}