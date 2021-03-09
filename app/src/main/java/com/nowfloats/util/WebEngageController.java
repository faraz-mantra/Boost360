package com.nowfloats.util;

import com.appsflyer.AppsFlyerLib;
import com.framework.analytics.FirebaseAnalyticsUtilsHelper;
import com.framework.analytics.NFWebEngageController;
import com.nowfloats.Login.UserSessionManager;
import com.webengage.sdk.android.User;

import java.util.HashMap;

public class WebEngageController {

    public static User weUser;

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
        try {
            if (weUser == null && session.getUserProfileId() != null) {
                initiateUserLogin(session.getUserProfileId());
            }
            if (weUser != null) {
                weUser.setEmail(session.getUserProfileEmail());
                weUser.setFirstName(session.getUserProfileName());
                weUser.setPhoneNumber(session.getUserProfileMobile());
                weUser.setCompany(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            }

            //Firebase Analytics User Property.
            FirebaseAnalyticsUtilsHelper.setUserProperty("emailId", session.getUserProfileEmail());
            FirebaseAnalyticsUtilsHelper.setUserProperty("name", session.getUserProfileName());
            FirebaseAnalyticsUtilsHelper.setUserProperty("mobile", session.getUserPrimaryMobile());
            FirebaseAnalyticsUtilsHelper.setUserProperty("Company", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));

            //AppsFlyer Analytics User Property.
            AppsFlyerLib.getInstance().setUserEmails(session.getUserProfileEmail());
            HashMap<String,Object> params=new HashMap<>();
            params.put("name",session.getUserProfileName());
            params.put("mobile",session.getUserPrimaryMobile());
            params.put("Company",session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            AppsFlyerLib.getInstance().setAdditionalData(params);


        } catch (Exception e) {
        }
    }

    public static void setFPTag(String fpTag) {
        NFWebEngageController.INSTANCE.setFPTag(fpTag);
    }

    public static void logout() {
        NFWebEngageController.INSTANCE.logout();
    }
}
