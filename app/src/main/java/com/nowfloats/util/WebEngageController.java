package com.nowfloats.util;

import android.se.omapi.Session;

import com.nowfloats.Login.UserSessionManager;
import com.webengage.sdk.android.Analytics;
import com.webengage.sdk.android.User;
import com.webengage.sdk.android.WebEngage;

import java.util.HashMap;
import java.util.Map;

public class WebEngageController {

    static Analytics weAnalytics = WebEngage.get().analytics();
    public static User weUser;


    public static void trackEvent(String event_name,String event_label,String event_value)
    {

        Map<String, Object> trackEvent = new HashMap<>();
        trackEvent.put("event_name", event_name);
        trackEvent.put("fptag", event_value);
        trackEvent.put("event_label",event_label);
        weAnalytics.track(event_name,trackEvent);

    }


    public static void setWebEngageProperties(UserSessionManager session) {


        weUser = WebEngage.get().user();
        weUser.login(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
        weUser.setEmail(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        weUser.setFirstName(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME));
        weUser.setPhoneNumber(session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
        weUser.setCompany(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
    }

    public static void logout(){

        weUser.logout();
    }

















}
