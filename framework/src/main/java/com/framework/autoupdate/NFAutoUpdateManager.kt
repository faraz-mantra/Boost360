package com.framework.autoupdate

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability


object NFAutoUpdateManager {
    const val TAG = "NFAutoUpdateManager"
    const val DAYS_FOR_FLEXIBLE_UPDATE = 1
    const val AUTO_UPDATE_REQUEST_CODE = 122;

    fun checkUpdate(activity: Activity) {
        val appUpdateManager = AppUpdateManagerFactory.create(activity)

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && (appUpdateInfo.clientVersionStalenessDays()
                            ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                update(appUpdateManager, appUpdateInfo, activity)
            }
        }
    }

    private fun update(appUpdateManager: AppUpdateManager, appUpdateInfo: AppUpdateInfo, activity: Activity) {


        appUpdateManager.startUpdateFlowForResult(
                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                appUpdateInfo,
                // The current activity making the update request.
                activity,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                        .setAllowAssetPackDeletion(true)
                        .build(),
                // Include a request code to later monitor this update request.
                AUTO_UPDATE_REQUEST_CODE)
    }

}