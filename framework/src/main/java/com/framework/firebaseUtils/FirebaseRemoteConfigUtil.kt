package com.framework.firebaseUtils

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.framework.R
import com.framework.utils.InAppReviewUtils
import com.framework.utils.convertJsonToObj
import com.google.android.play.core.install.model.AppUpdateType
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

const val FIREBASE_RC_FETCH_INTERVAL: Long = 900
const val FESTIVE_POSTER_NAME = "festive_poster_name"
const val DASHBOARD_FESTIVAL_BUTTON_VISIBILITY = "dashboard_festive_poster_button_visible"
const val FEATURE_DOMAIN_BOOKING_ENABLE = "feature_domain_booking_enable"
const val IN_APP_UPDATE_TYPE_IMMEDIATE = "in_app_update_type_immediate"
const val K_ADMIN_URL = "k_admin_url"
const val NEW_ONBOARDING_WITH_UPDATED_CATEGORIES_AND_GUI_ACTIVE = "new_onboarding_with_updated_categories_and_gui_active"
const val FEATURE_ERROR_HANDLING_ENABLE = "feature_error_handling_enable"
const val FEATURE_UPDATE_STUDIO_SELECTED_USERS="feature_update_studio_selected_users"
const val PERMISSION_FACEBOOK = "permissions_facebook"

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

  fun kAdminUrl(): String? {
    Log.d(TAG, "kAdminUrl: ${remoteConfig?.getString(K_ADMIN_URL)}")
    return remoteConfig?.getString(K_ADMIN_URL)
  }

  fun featureDomainEnable(): Boolean {
    Log.d(TAG, "Config feature domain enable: ${remoteConfig?.getBoolean(FEATURE_DOMAIN_BOOKING_ENABLE) ?: false}")
    return remoteConfig?.getBoolean(FEATURE_DOMAIN_BOOKING_ENABLE) ?: false
  }

  fun featureErrorHandlingEnable(): Boolean {
    Log.d(TAG, "featureErrorHandlingEnable: ${remoteConfig?.getBoolean(FEATURE_ERROR_HANDLING_ENABLE) ?: false}")
    return remoteConfig?.getBoolean(FEATURE_ERROR_HANDLING_ENABLE) ?: false
  }

  fun featureNewOnBoardingFlowEnable(): Boolean {
    Log.d(TAG, "Config feature NEW ONBOARDING enable: ${remoteConfig?.getBoolean(NEW_ONBOARDING_WITH_UPDATED_CATEGORIES_AND_GUI_ACTIVE) ?: false}")
    return remoteConfig?.getBoolean(NEW_ONBOARDING_WITH_UPDATED_CATEGORIES_AND_GUI_ACTIVE) ?: true
  }

  fun appUpdateType(): Int {
    Log.d(TAG, "Config in app update type: ${remoteConfig?.getString(IN_APP_UPDATE_TYPE_IMMEDIATE) ?: false}")
    return if (remoteConfig?.getBoolean(IN_APP_UPDATE_TYPE_IMMEDIATE) == true) {
      AppUpdateType.IMMEDIATE
    } else {
      AppUpdateType.FLEXIBLE
    }
  }

  fun permissionListFacebookPage(): List<String> {
    Log.d(TAG, "permission list facebook page: ${remoteConfig?.getString(PERMISSION_FACEBOOK) ?: ""}")
    return (remoteConfig?.getString(PERMISSION_FACEBOOK) ?: "").split(",")
  }

  fun isInAppReviewFlagEnabled(event: InAppReviewUtils.Events): Boolean {
    val status = remoteConfig?.getBoolean(event.name)
    return status ?: false
  }

  fun featureUpdateStudioSelectedUsers(fpTag:String?): Boolean {
    val selectedFps =remoteConfig?.getString(FEATURE_UPDATE_STUDIO_SELECTED_USERS)

    return  selectedFps?.contains(fpTag?:"")==true
  }

}

enum class UpdateType {
  FLEXIBLE,
  IMMEDIATE
}
