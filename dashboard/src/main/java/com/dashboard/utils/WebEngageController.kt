package com.dashboard.utils

import com.appsflyer.AppsFlyerLib
import com.framework.analytics.FirebaseAnalyticsUtilsHelper
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

  fun setUserContactAttributes(email: String?, mobile: String?, name: String?, company: String?) {
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
      if (!company.isNullOrEmpty()) {
        weUser.setCompany(company)

        //Firebase Analytics User Property.
        FirebaseAnalyticsUtilsHelper.setUserProperty("company", company)
      }
    }
  }

  fun setFPTag(fpTag: String?) {
    try {
//      weUser.setAttribute("fpTag", fpTag)
//      //Firebase Analytics User Property.
//      FirebaseAnalyticsUtils.setUserProperty("fpTag", fpTag?:"")
    } catch (e: java.lang.Exception) {
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

  fun trackEvent(event_name: String = "", event_label: String = "", event_value: String? = "") {
    try {
      val trackEvent: MutableMap<String, Any> = HashMap()
      trackEvent["event_name"] = event_name
      trackEvent["fptag/event_value"] = event_value?:""
      trackEvent["event_label"] = event_label
      weAnalytics.track(event_name, trackEvent)

      //Firebase Analytics Event...
      FirebaseAnalyticsUtilsHelper.logDefinedEvent(event_name, event_label, event_value?:"")

      //AppsFlyerEvent...
      AppsFlyerLib.getInstance().logEvent(weAnalytics.activity.get()?.applicationContext, event_name, trackEvent);
    } catch (e: Exception) {
    }
  }


  fun logout() {
    try {
      weUser.logout()
      //Reset Firebase Analytics User Session Event.
      FirebaseAnalyticsUtilsHelper.resetIdentifyUser()
      //End AppsFlyer Analytics User Session Event.
      AppsFlyerLib.getInstance().setCustomerUserId(null)
    } catch (e: Exception) {
    }
  }
}