package com.nowfloats.util;

import android.text.TextUtils;

import com.framework.analytics.FirebaseAnalyticsUtils;
import com.nowfloats.Login.UserSessionManager;
import com.webengage.sdk.android.Analytics;
import com.webengage.sdk.android.User;
import com.webengage.sdk.android.WebEngage;

import java.util.HashMap;
import java.util.Map;

public class WebEngageController {

    public static User weUser;
    static Analytics weAnalytics = WebEngage.get().analytics();

    public static void trackEvent(String event_name, String event_label, String event_value) {
        try {
            Map<String, Object> trackEvent = new HashMap<>();
            trackEvent.put("event_name", event_name);
            trackEvent.put("event_label", event_label);
            trackEvent.put("event_value", event_value);
            weAnalytics.track(event_name, trackEvent);

            //Firebase Analytics Event...
            FirebaseAnalyticsUtils.logDefinedEvent(event_name, event_label, TextUtils.isEmpty(event_value) ? "" : event_value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initiateUserLogin(String profileId) {
        weUser = WebEngage.get().user();
        weUser.login(profileId);

        //Firebase Analytics User Session Event.
        FirebaseAnalyticsUtils.identifyUser(profileId);

    }

    public static void setUserContactInfoProperties(String profileId, String email, String mobile, String name) {
        try {
            if (weUser == null && profileId != null) {
                initiateUserLogin(profileId);
            }
            if (weUser != null) {
                if (email != null && email.length() > 0) {
                    weUser.setEmail(email);

                    //Firebase Analytics User Property.
                    FirebaseAnalyticsUtils.setUserProperty("emailId", email);
                }

                if (name != null && name.length() > 0) {
                    weUser.setFirstName(name);

                    //Firebase Analytics User Property.
                    FirebaseAnalyticsUtils.setUserProperty("name", name);
                }

                if (mobile != null && mobile.length() > 0) {
                    weUser.setPhoneNumber(mobile);

                    //Firebase Analytics User Property.
                    FirebaseAnalyticsUtils.setUserProperty("mobile", mobile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            FirebaseAnalyticsUtils.setUserProperty("emailId", session.getUserProfileEmail());
            FirebaseAnalyticsUtils.setUserProperty("name", session.getUserProfileName());
            FirebaseAnalyticsUtils.setUserProperty("mobile", session.getUserPrimaryMobile());
            FirebaseAnalyticsUtils.setUserProperty("Company", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFPTag(String fpTag) {
        try {
            if (weUser != null) {
                weUser.setAttribute("fpTag", fpTag);
            }

            //Firebase Analytics User Property.
            FirebaseAnalyticsUtils.setUserProperty("fpTag", fpTag);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        if (weUser != null) weUser.logout();
        //Reset Firebase Analytics User Session Event.
        FirebaseAnalyticsUtils.resetIdentifyUser();
    }
}
