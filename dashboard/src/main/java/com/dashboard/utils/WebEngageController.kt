package com.dashboard.utils

import com.framework.analytics.NFWebEngageController
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId

object WebEngageController {

  fun initiateUserLogin(userId: String?) = NFWebEngageController.initiateUserLogin(userId)

  fun setUserContactInfoProperties(session: UserSessionManager) {
    initiateUserLogin(session.userProfileId)
    setUserContactAttributes(
      session.userProfileEmail,
      session.userPrimaryMobile,
      session.userProfileName,
      clientId
    )
    NFWebEngageController.setCategory(session.fP_AppExperienceCode)
  }

  fun setUserContactAttributes(email: String?, mobile: String?, name: String?, clientId: String?) =
    NFWebEngageController.setUserContactAttributes(email, mobile, name, clientId)

  fun trackEvent(event_name: String = "", event_label: String = "", event_value: String? = "") =
    NFWebEngageController.trackEvent(event_name, event_label, event_value ?: "")

  fun setFPTag(fpTag: String?) = NFWebEngageController.setFPTag(fpTag ?: "")

  fun logout() = NFWebEngageController.logout()
}