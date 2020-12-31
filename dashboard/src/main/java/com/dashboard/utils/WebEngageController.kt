package com.dashboard.utils

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
      }
      if (!mobile.isNullOrEmpty()) {
        weUser.setPhoneNumber(mobile)
      }
      if (!name.isNullOrEmpty()) {
        weUser.setFirstName(name)
      }

      if (!company.isNullOrEmpty()) {
        weUser.setCompany(company)
      }

//            //Firebase Analytics User Property.
//            FirebaseAnalyticsUtils.setUserProperty("emailId", session.getUserProfileEmail())
//            FirebaseAnalyticsUtils.setUserProperty("name", session.getUserProfileName())
//            FirebaseAnalyticsUtils.setUserProperty("mobile", session.getUserPrimaryMobile())
//            FirebaseAnalyticsUtils.setUserProperty("Company", session.getFPDetails(com.nowfloats.util.Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME))

    }
  }

  fun setFPTag(fpTag: String?) {
    try {
//            if (com.nowfloats.util.WebEngageController.weUser != null) {
//                com.nowfloats.util.WebEngageController.weUser.setAttribute("fpTag", fpTag)
//            }
//
//            //Firebase Analytics User Property.
//            FirebaseAnalyticsUtils.setUserProperty("fpTag", fpTag)
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

  fun trackEvent(event_name: String, event_label: String, event_value: String?) {
    try {
      val trackEvent: MutableMap<String, Any> = HashMap()
      trackEvent["event_name"] = event_name
      trackEvent["fptag/event_value"] = event_value?:""
      trackEvent["event_label"] = event_label
      weAnalytics.track(event_name, trackEvent)
    }catch (e:Exception){
      e.printStackTrace()
    }
  }


  fun logout() {
    weUser.logout()
  }
}