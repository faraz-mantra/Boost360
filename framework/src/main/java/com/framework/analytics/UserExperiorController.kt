package com.framework.analytics

import android.app.Application
import android.os.Build
import android.util.Log
import com.framework.BuildConfig
import com.framework.pref.UserSessionManager
import com.framework.utils.getAppVersionName
import com.google.gson.Gson
import com.userexperior.UserExperior



object UserExperiorController {

    private const val TAG = "UserExperiorController"
    val DEV_KEY="971021d8-3b0a-45e0-80f1-212150a16791"
    val LIVE_KEY="0efa1c36-15ec-43ba-93e7-e00ff1355f4b"

    fun startRecording(application: Application){
        UserExperior.startRecording(application, DEV_KEY);
    }


    fun setUserIdentifier(fpId:String?){
        UserExperior.setUserIdentifier(fpId)
    }

    fun setUserAttr(fpTag:String?,mobile: String?, business_name: String?,
                    category: String?,name:String?,app_version:String?){
        val userProperties: HashMap<String, Any?> = HashMap()

        setUserIdentifier(fpTag)
        userProperties["name"] = name
        userProperties["mobile"] =mobile
        userProperties["business_name"] = business_name
        userProperties["category"] = category
        userProperties["app_version"] = app_version
        Log.i(TAG, "setUserAttr: ${Gson().toJson(userProperties)}")
        UserExperior.setUserProperties(userProperties)
    }

    fun setUserAttr(session:UserSessionManager){
        setUserAttr(session.fPID,session.userProfileMobile,session.fPName,session.fpTag,session.userProfileName,
            getAppVersionName())
    }

    fun trackEvent(eventName: String,event_value: HashMap<String, Any>?=null){
        UserExperior.logEvent(eventName,event_value)
    }

    fun logout(){
        UserExperior.logEvent("logout")
    }

    fun setFpTag(fp:String){

    }
}