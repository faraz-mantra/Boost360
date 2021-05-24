package com.framework.analytics

import com.appsflyer.AppsFlyerLib
import com.webengage.sdk.android.Analytics
import com.webengage.sdk.android.User
import com.webengage.sdk.android.WebEngage

object NFWebEngageController {
    private var weAnalytics: Analytics = WebEngage.get().analytics()
    private var weUser: User = WebEngage.get().user()
    private var isUserLoggedIn = false


    fun trackEvent(event_name: String, event_label: String, event_value: String) {
        val trackEvent: MutableMap<String, Any> = HashMap()
        trackEvent["event_name"] = event_name
        trackEvent["fptag/event_value"] = event_value
        trackEvent["event_label"] = event_label
        if(event_label.equals("rev")){
            trackEvent["revenue"] = event_value
        }
        weAnalytics.track(event_name, trackEvent)
        weAnalytics.screenNavigated(event_name)
        //Firebase Analytics Event...
        FirebaseAnalyticsUtilsHelper.logDefinedEvent(event_name, event_label, event_value)

        //AppsFlyerEvent...
        try {
            AppsFlyerLib.getInstance().logEvent(weAnalytics.activity.get()?.applicationContext, event_name, trackEvent.toMap())
        } catch (e: Exception) {
        }
    }

    fun trackEvent(event_name: String, event_label: String, event_value: HashMap<String, Any>) {
        if (event_value.size > 0) {
            weAnalytics.track(event_name, event_value)
            weAnalytics.screenNavigated(event_name)

            //Firebase Analytics Event...
            FirebaseAnalyticsUtilsHelper.logDefinedEvent(event_name, event_label, "")

            //AppsFlyerEvent...
            try {
                AppsFlyerLib.getInstance().logEvent(weAnalytics.activity.get()?.applicationContext,
                        event_name, event_value.toMap())
            } catch (e: Exception) {
            }
        } else {
            weAnalytics.track(event_name)
            weAnalytics.screenNavigated(event_name)
        }
    }

    fun trackEventLoad(event_name: String, event_label: String, event_value: HashMap<String, Any>, value:String) {
        if (event_value.size > 0) {
            event_value["event_name"] = event_name
            event_value["event_label"] = event_label
            weAnalytics.track(event_name, event_value)
            weAnalytics.screenNavigated(event_name)

            //Firebase Analytics Event...
            FirebaseAnalyticsUtilsHelper.logDefinedEvent(event_name, event_label, "")

            //AppsFlyerEvent...
            try {
                AppsFlyerLib.getInstance().logEvent(weAnalytics.activity.get()?.applicationContext,
                        event_name, event_value.toMap())
            } catch (e: Exception) {
            }
        } else {
            weAnalytics.track(event_name)
            weAnalytics.screenNavigated(event_name)
        }
    }

    fun setUserContactAttributes(email: String?, mobile: String?, name: String?, clientId: String? = "") {
        if (isUserLoggedIn) {
            if (!email.isNullOrEmpty()) {
                weUser.setEmail(email)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("emailId", email)

                //AppsFlyer Analytics User Property.
                AppsFlyerLib.getInstance().setUserEmails(email)
            }

            //AppsFlyer Analytics User Property.
            val params = HashMap<String, Any>()

            if (!mobile.isNullOrEmpty()) {
                weUser.setPhoneNumber(mobile)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("mobile", mobile)
                params["mobile"] = mobile
            }
            if (!name.isNullOrEmpty()) {
                weUser.setFirstName(name)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("name", name)
                params["name"] = name
            }
            if (!clientId.isNullOrEmpty()) {
                weUser.setAttribute("clientId", clientId)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("clientId", clientId)
                params["clientId"] = clientId
            }
            if (params.isNotEmpty())
                AppsFlyerLib.getInstance().setAdditionalData(params)
        }
    }


    fun initiateUserLogin(userId: String?) {
        if (userId != null && !userId.isNullOrEmpty()) {
            weUser.login(userId)

            //Firebase Analytics User Session Event.
            FirebaseAnalyticsUtilsHelper.identifyUser(userId)

            //AppsFlyer Analytics User Session Event
            if (weAnalytics.activity!=null) {
                AppsFlyerLib.getInstance().logSession(weAnalytics.activity.get()?.applicationContext)
            }
            AppsFlyerLib.getInstance().setCustomerUserId(userId)
            isUserLoggedIn = true
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

    fun setFPTag(fpTag: String) {
        try {
            weUser.setAttribute("fpTag", fpTag)

            //Firebase Analytics User Property.
            FirebaseAnalyticsUtilsHelper.setUserProperty("fpTag", fpTag)

            //AppsFlyer User Property
            val params = java.util.HashMap<String, Any>()
            params["fpTag"] = fpTag
            AppsFlyerLib.getInstance().setAdditionalData(params)
        } catch (e: java.lang.Exception) {
        }
    }

    fun logout() {
        weUser.logout()

        //Reset Firebase Analytics User Session Event.
        FirebaseAnalyticsUtilsHelper.resetIdentifyUser()

        //End AppsFlyer Analytics User Session Event.
        AppsFlyerLib.getInstance().setCustomerUserId(null)
    }
}