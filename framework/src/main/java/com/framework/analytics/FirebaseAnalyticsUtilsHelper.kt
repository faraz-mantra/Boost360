package com.framework.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


object FirebaseAnalyticsUtilsHelper {

    // Obtain the FirebaseAnalytics instance.
    private var firebaseAnalytics: FirebaseAnalytics = Firebase.analytics


    /**
     * This function is used to send the event to Firebase and log the output.
     *
     * @param eventName     The name of the event.
     * @param eventBundle   The Bundle for the event.
     */
    private fun logFirebaseEvent(eventName: String, eventBundle: Bundle) {
        firebaseAnalytics.logEvent(eventName, eventBundle)
    }

    @JvmStatic
    fun logEvent(eventName: String, eventBundle: Bundle) {
        logFirebaseEvent(eventName, eventBundle)
    }

    @JvmStatic
    fun logEvent(eventName: String, eventLabel: String, eventValue: String) {
        val event = FirebaseEventHelper(eventName)
        event.putString(eventLabel, eventValue)
        logFirebaseEvent(event.getName(), event.getBundle())
    }

    @JvmStatic
    fun logDefinedEvent(eventName: String, eventLabel: String, eventValue: String) {
        val event = FirebaseEventHelper(eventName)
        event.putString("event_name", eventName)
        event.putString("fptag/event_value", eventLabel)
        event.putString("event_label", eventValue)
        logFirebaseEvent(event.getName(), event.getBundle())
    }


    /**
     * This function is set to identify the user for subsequent calls
     *
     * @param userID   - User Id from the server
     */
    @JvmStatic
    fun identifyUser(userID: String) {
        firebaseAnalytics.setUserId(userID)
    }

    @JvmStatic
    fun setUserProperty(label: String, value: String) {
        firebaseAnalytics.setUserProperty(label, value)
    }

    /**
     * This resets the Identify user once the user has logged out
     */
    @JvmStatic
    fun resetIdentifyUser() {
        firebaseAnalytics.setUserId(null)
    }

}