package com.nowfloats.facebook.managers

import android.app.Activity
import android.os.Bundle
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger


object FBEventManager {

    var logger : AppEventsLogger? = null;
    fun logEvent(eventName: String?, activity: Activity){
        FacebookSdk.setIsDebugEnabled(true);
        var logger = AppEventsLogger.newLogger(activity);
        logger.logEvent(eventName);
    }



    fun logAddToCartEvent(activity: Activity) {
        FacebookSdk.setIsDebugEnabled(true);
        val params = Bundle()
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, "TestContentData")
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, "TestContentID")
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "TestContentType")
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "INR")
        var logger = AppEventsLogger.newLogger(activity);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, 2000.0, params)
    }
}