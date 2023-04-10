package com.framework.analytics

import android.app.Activity
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.clevertap.android.sdk.CleverTapAPI
import com.framework.BaseApplication
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.webengage.sdk.android.Analytics
import com.webengage.sdk.android.WebEngage

object CleverTapController {

    private var weAnalytics: Analytics = WebEngage.get().analytics()
    private val activity: Activity?
        get() {
            return if (weAnalytics.activity!=null) weAnalytics.activity.get() else BaseApplication.currentActivity()
        }
    var weUser = HashMap<String, String>()
    private var isUserLoggedIn = false
    private val TAG = "NFController"


    fun trackAttributeCleverTap(event_value: HashMap<String, Any>) {
        if (event_value.isNullOrEmpty().not()) {
            for ((key, value) in event_value.entries) {
                weUser.put(key, value.toString())
                FirebaseAnalyticsUtilsHelper.setUserProperty(key, value.toString())
            }
            AppsFlyerLib.getInstance().setAdditionalData(event_value)
        }
    }

    fun trackEventCleverTap(event_name: String, event_label: String, event_value: String? = NO_EVENT_VALUE) {
        val trackEvent: MutableMap<String, Any> = HashMap()
        val cleverTapInstance = CleverTapAPI.getDefaultInstance(WebEngage.getApplicationContext())
        trackEvent["event_name"] = event_name
        trackEvent["fptag/event_value"] = event_value?:""
        trackEvent["event_label"] = event_label
        if (event_label == "rev") {
            trackEvent["revenue"] = event_value?:""
        }
        cleverTapInstance?.pushEvent(event_name, trackEvent)
        cleverTapInstance?.recordScreen(event_name)
        //Firebase Analytics Event...
        FirebaseAnalyticsUtilsHelper.logDefinedEvent(event_name, event_label, event_value?:"")
        UserExperiorController.trackEvent(event_name, HashMap(trackEvent))
        //AppsFlyerEvent...
        try {
            AppsFlyerLib.getInstance().logEvent(activity?.applicationContext, event_name, trackEvent.toMap())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun trackEventCleverTap(event_name: String, event_label: String, event_value: HashMap<String, Any>) {
        val cleverTapInstance = CleverTapAPI.getDefaultInstance(WebEngage.getApplicationContext())
        if (event_value.size > 0) {
            cleverTapInstance?.pushEvent(event_name, event_value)
            cleverTapInstance?.recordScreen(event_name)
            UserExperiorController.trackEvent(event_name, event_value)

            //Firebase Analytics Event...
            FirebaseAnalyticsUtilsHelper.logDefinedEvent(event_name, event_label, "")

            //AppsFlyerEvent...
            try {
                AppsFlyerLib.getInstance().logEvent(activity?.applicationContext, event_name, event_value.toMap())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            cleverTapInstance?.pushEvent(event_name, event_value)
            cleverTapInstance?.recordScreen(event_name)
            UserExperiorController.trackEvent(event_name)
        }
    }

    fun trackEventLoadCleverTap(event_name: String, event_label: String, event_value: HashMap<String, Any>, value: String) {
        val cleverTapInstance = CleverTapAPI.getDefaultInstance(WebEngage.getApplicationContext())
        if (event_value.size > 0) {
            event_value["event_name"] = event_name
            event_value["event_label"] = event_label
            cleverTapInstance?.pushEvent(event_name, event_value)
            cleverTapInstance?.recordScreen(event_name)
            UserExperiorController.trackEvent(event_name, event_value)

            //Firebase Analytics Event...
            FirebaseAnalyticsUtilsHelper.logDefinedEvent(event_name, event_label, "")

            //AppsFlyerEvent...
            try {
                AppsFlyerLib.getInstance().logEvent(activity?.applicationContext, event_name, event_value.toMap())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            cleverTapInstance?.pushEvent(event_name, event_value)
            cleverTapInstance?.recordScreen(event_name)
            UserExperiorController.trackEvent(event_name)
        }
    }

    fun setUserContactAttributesCleverTap(email: String?, mobile: String?, name: String?, clientId: String? = "") {
        if (isUserLoggedIn) {
            if (!email.isNullOrEmpty()) {
                weUser.put("Email",email)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("emailId", email)

                //AppsFlyer Analytics User Property.
                AppsFlyerLib.getInstance().setUserEmails(email)
            }

            //AppsFlyer Analytics User Property.
            val params = HashMap<String, Any>()

            if (!mobile.isNullOrEmpty()) {
                val plusMobile=getNumberPlus91(mobile)
                weUser.put("PhoneNumber",plusMobile)
                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("mobile", plusMobile)
                params["mobile"] = plusMobile
            }
            if (!name.isNullOrEmpty()) {
                weUser.put("FirstName",name)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("name", name)
                params["name"] = name
            }
            if (!clientId.isNullOrEmpty()) {
                weUser.put("clientId", clientId)

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.setUserProperty("clientId", clientId)
                params["clientId"] = clientId
            }
            if (params.isNotEmpty())
                AppsFlyerLib.getInstance().setAdditionalData(params)
        }
    }


    fun initiateUserLoginCleverTap(userId: String?) {
        val cleverTapInstance = CleverTapAPI.getDefaultInstance(WebEngage.getApplicationContext())
        if (userId != null && !userId.isNullOrEmpty()) {
            Log.d(TAG, "Initiating User login$userId")
            cleverTapInstance?.onUserLogin(weUser as Map<String, Any>?);

            //Firebase Analytics User Session Event.
            FirebaseAnalyticsUtilsHelper.identifyUser(userId)

            //AppsFlyer Analytics User Session Event
            if (activity != null) {
                AppsFlyerLib.getInstance().logSession(activity?.applicationContext)
            }
            AppsFlyerLib.getInstance().setCustomerUserId(userId)
            isUserLoggedIn = true

        }
    }

    fun setCategoryCleverTap(userCategory: String?) {
        try {
            if (!userCategory.isNullOrEmpty()) {
                val version = activity?.packageManager?.getPackageInfo(activity?.packageName?:"", 0)?.versionName
                weUser.put("Category", userCategory)
                weUser.put("Version", version ?: "")

                //Firebase Analytics User Property.
                FirebaseAnalyticsUtilsHelper.apply {
                    setUserProperty("Category", userCategory)
                    setUserProperty("Version", version ?: "")
                }

                //AppsFlyer User Property
                val params = HashMap<String, Any>()
                params["Category"] = userCategory
                params["Version"] = version ?: ""
                AppsFlyerLib.getInstance().setAdditionalData(params)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setFPTagCleverTap(fpTag: String?) {
        try {
            if (fpTag == null) return;
            Log.d(TAG, "Setting FP Tag$fpTag")
            weUser.put("fpTag", fpTag)
            UserExperiorController.setFpTag(fpTag)

            //Firebase Analytics User Property.
            FirebaseAnalyticsUtilsHelper.setUserProperty("fpTag", fpTag)

            //AppsFlyer User Property
            val params = java.util.HashMap<String, Any>()
            params["fpTag"] = fpTag
            AppsFlyerLib.getInstance().setAdditionalData(params)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun logout() {
        Log.d(TAG, "Loggind user out from analytics")
        UserExperiorController.logout()

        weUser.clear()
        //Reset Firebase Analytics User Session Event.
        FirebaseAnalyticsUtilsHelper.resetIdentifyUser()

        //End AppsFlyer Analytics User Session Event.
        AppsFlyerLib.getInstance().setCustomerUserId(null)
    }

    fun getNumberPlus91(number:String): String {
        return when {
            number.contains("+91-") -> number.replace("+91-", "+91")
            !number.contains("+91") -> "+91$number"
            else -> number
        }
    }

    fun setFPIdCleverTap(fpId: String) {
        weUser.put("Identity", fpId)
    }
}