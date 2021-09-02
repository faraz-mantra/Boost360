package com.framework.analytics

import android.app.Application
import com.userexperior.UserExperior



object UserExperiorController {

    val DEV_KEY="971021d8-3b0a-45e0-80f1-212150a16791"
    val LIVE_KEY="0efa1c36-15ec-43ba-93e7-e00ff1355f4b"

    fun startRecording(application: Application){
        UserExperior.startRecording(application, DEV_KEY);
    }


    fun setUserIdentifier(userId:String){
        UserExperior.setUserIdentifier(userId)
    }
    fun setUserattr(email: String?, mobile: String?, name: String?, clientId: String? = ""){
        val userProperties: HashMap<String, Any?> = HashMap()
        userProperties["name"] = name
        userProperties["mobile"] =mobile
        userProperties["email"] = email
        userProperties["clientId"] = clientId

        UserExperior.setUserProperties(userProperties)
    }

    fun trackEvent(eventName: String,event_value: HashMap<String, Any>?=null){
        UserExperior.logEvent(eventName,event_value)
    }

    fun logout(){
        UserExperior.logEvent("logout")
    }

    fun setFpTag(fp:String){
        UserExperior.setUserProperties(hashMapOf("" +
                "fpTag" to fp))
    }
}