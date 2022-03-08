package com.framework.analytics

import android.app.Application
import android.os.Build
import android.util.Log
import com.framework.BuildConfig
import com.framework.pref.UserSessionManager
import com.framework.utils.getAppVersionName
import com.google.gson.Gson
import com.userexperior.UserExperior
import java.lang.Exception


object UserExperiorController {

    private const val TAG = "UserExperiorController"

    fun startRecording(application: Application){
        UserExperior.startRecording(application, BuildConfig.USER_EXPERIOR_LIVE_KEY);
    }


    fun setUserIdentifier(fpId:String?){
        UserExperior.setUserIdentifier(fpId)
    }

    fun setUserAttr(fpID:String?,mobile: String?, business_name: String?,
                    category: String?,name:String?,app_version:String?){
        val userProperties: HashMap<String, Any?> = HashMap()

        setUserIdentifier(fpID)
//        userProperties["name"] = name
//        userProperties["mobile"] =mobile
        userProperties["business_name"] = business_name
        userProperties["category"] = category
        userProperties["app_version"] = app_version
        Log.i(TAG, "setUserAttr: ${Gson().toJson(userProperties)}")
        UserExperior.setUserProperties(userProperties)
    }

    fun setUserAttr(session:UserSessionManager){
        setUserAttr(session.fPID,session.userProfileMobile,session.fPName,session.fP_AppExperienceCode,session.userProfileName,
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

    fun startScreen(screenName:String){
        try {
            UserExperior.startScreen(screenName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}