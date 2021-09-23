package com.framework.autoupdate

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


object NFAutoUpdateManager {
    const val TAG = "NFAutoUpdateManager"
   // const val DAYS_FOR_FLEXIBLE_UPDATE = 1
    const val AUTO_UPDATE_REQUEST_CODE = 122;
    lateinit var installStateUpdatedListener: InstallStateUpdatedListener
    var appUpdateManager:AppUpdateManager?=null


    fun checkUpdate(activity: Activity) {
        Log.i(TAG, "checkUpdate: ")
        appUpdateManager = AppUpdateManagerFactory.create(activity)

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                update(appUpdateManager, appUpdateInfo, activity)
            }
        }

        installStateUpdatedListener = InstallStateUpdatedListener {
            if (it.installStatus()==InstallStatus.DOWNLOADED){
                showSnackBarComplete(activity,appUpdateManager)
            }
            Toast.makeText(activity, "install status: "+it.installStatus(), Toast.LENGTH_SHORT).show()
        }
        appUpdateManager?.registerListener(installStateUpdatedListener)

    }

    private fun showSnackBarComplete(activity: Activity, appUpdateManager: AppUpdateManager?) {
        val snackBar = Snackbar.make(activity.findViewById(android.R.id.content),
            "New app is ready",Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Install") {
            appUpdateManager?.completeUpdate()
        }
        snackBar.show()
    }

    private fun update(appUpdateManager: AppUpdateManager?, appUpdateInfo: AppUpdateInfo, activity: Activity) {


        appUpdateManager?.startUpdateFlowForResult(
                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                appUpdateInfo,
                // The current activity making the update request.
                activity,
                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                        .setAllowAssetPackDeletion(true)
                        .build(),
                // Include a request code to later monitor this update request.
                AUTO_UPDATE_REQUEST_CODE)
    }

    fun stopListener(){
        appUpdateManager?.unregisterListener(installStateUpdatedListener)
    }
}