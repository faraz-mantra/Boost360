package com.framework.firebaseUtils

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.framework.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

const val FIREBASE_RC_FETCH_INTERVAL: Long = 900
const val FESTIVE_POSTER_NAME = "festive_poster_name"
const val DASHBOARD_FESTIVAL_BUTTON_VISIBILITY = "dashboard_festive_poster_button_visible"
const val FEATURE_DOMAIN_BOOKING_ENABLE = "feature_domain_booking_enable"
const val IN_APP_UPDATE_TYPE_IMMEDIATE = "in_app_update_type_immediate"

object FirebaseRemoteConfigUtil {

  val TAG = "FirebaseRemoteConfig"
  var remoteConfig: FirebaseRemoteConfig? = null

  fun initRemoteConfigData(activity: AppCompatActivity) {
    remoteConfig = Firebase.remoteConfig
    remoteConfig?.setConfigSettingsAsync(remoteConfigSettings { minimumFetchIntervalInSeconds = FIREBASE_RC_FETCH_INTERVAL })
    remoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
    activity.fetchRemoteConfig()
  }


  fun AppCompatActivity.fetchRemoteConfig() {
    remoteConfig?.fetchAndActivate()?.addOnCompleteListener(this) { task ->
      if (task.isSuccessful) {
        val updated = task.result
        Log.d(TAG, "Config params updated: $updated")
      }
    }
  }

  fun festivePosterVisibility(): Boolean {
    Log.d(TAG, "Config festive poster visibility: ${remoteConfig?.getBoolean(DASHBOARD_FESTIVAL_BUTTON_VISIBILITY) ?: false}")
    return remoteConfig?.getBoolean(DASHBOARD_FESTIVAL_BUTTON_VISIBILITY) ?: false
  }

  fun festivePosterName(): String? {
    Log.d(TAG, "Config festive poster name: ${remoteConfig?.getBoolean(DASHBOARD_FESTIVAL_BUTTON_VISIBILITY) ?: false}")
    return remoteConfig?.getString(FESTIVE_POSTER_NAME)
  }

  fun featureDomainEnable(): Boolean {
    Log.d(TAG, "Config feature domain enable: ${remoteConfig?.getBoolean(FEATURE_DOMAIN_BOOKING_ENABLE) ?: false}")
    return remoteConfig?.getBoolean(FEATURE_DOMAIN_BOOKING_ENABLE) ?: false
  }

  fun appUpdateType(): UpdateType {
    Log.d(TAG, "Config in app update type: ${remoteConfig?.getString(IN_APP_UPDATE_TYPE_IMMEDIATE) ?: false}")
    return if (remoteConfig?.getBoolean(IN_APP_UPDATE_TYPE_IMMEDIATE) == true) {
      UpdateType.IMMEDIATE
    } else {
      UpdateType.FLEXIBLE
    }
  }
}

enum class UpdateType{
  FLEXIBLE,
  IMMEDIATE
}
