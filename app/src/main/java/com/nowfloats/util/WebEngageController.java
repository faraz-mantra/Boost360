package com.nowfloats.util;

import com.webengage.sdk.android.Analytics;
import com.webengage.sdk.android.WebEngage;

import java.util.HashMap;
import java.util.Map;

public class WebEngageController {

    static Analytics weAnalytics = WebEngage.get().analytics();



    public static void trackEvent(String event_name,String event_label,String event_value)
    {

        Map<String, Object> trackEvent = new HashMap<>();
        trackEvent.put(event_name, event_value);
        weAnalytics.track(event_label);

    }

















}
