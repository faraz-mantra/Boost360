package com.nowfloats.util;

import com.framework.analytics.NFWebEngageController;
import com.nowfloats.Login.UserSessionManager;

public class WebEngageController {

  public static void trackEvent(String event_name, String event_label, String event_value) {
    if (event_value == null) {
      event_value = "";
    }
    NFWebEngageController.INSTANCE.trackEvent(event_name, event_label, event_value);
  }

  public static void initiateUserLogin(String profileId) {
    NFWebEngageController.INSTANCE.initiateUserLogin(profileId);
  }

  public static void setUserContactInfoProperties(UserSessionManager session) {
    if (session.getUserProfileId() != null) {
      initiateUserLogin(session.getUserProfileId());
    }
    NFWebEngageController.INSTANCE.setUserContactAttributes(
        session.getUserProfileEmail(),
        session.getUserPrimaryMobile(),
        session.getUserProfileName(),
        session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
    );
  }

  public static void setFPTag(String fpTag) {
    NFWebEngageController.INSTANCE.setFPTag(fpTag);
  }

  public static void logout() {
    NFWebEngageController.INSTANCE.logout();
  }
}
