package com.framework.utils

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.framework.R
import com.framework.analytics.SentryController
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateUtil(val activity:Activity) {

    private lateinit var appUpdateManager: AppUpdateManager
    private val TAG = "InAppUpdateUtil"

    private val appUpdateListener: InstallStateUpdatedListener = InstallStateUpdatedListener { state ->
        when {
            state.installStatus() == InstallStatus.DOWNLOADED -> popupSnackBarForCompleteUpdate()
            state.installStatus() == InstallStatus.INSTALLED -> removeInstallStateUpdateListener()
        }
    }

    fun checkForUpdate(reqCode: Int,onComplete:()->Unit) {
        appUpdateManager = AppUpdateManagerFactory.create(activity)
        appUpdateManager.registerListener(appUpdateListener)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            Log.i(TAG, "checkForUpdate: ${appUpdateInfo.updateAvailability()} ${appUpdateInfo.isUpdateTypeAllowed(
                AppUpdateType.IMMEDIATE)}")
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ) {
                startUpdate(appUpdateInfo,reqCode,onComplete)
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            }else{
                onComplete.invoke()
            }
        }.addOnFailureListener {
            onComplete.invoke()
        }
    }

    private fun startUpdate(appUpdateInfo: AppUpdateInfo,reqCode:Int,onComplete: () -> Unit) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                FirebaseRemoteConfigUtil.appUpdateType(), activity, reqCode)
        } catch (e: IntentSender.SendIntentException) {
            onComplete.invoke()
            e.printStackTrace();
            SentryController.captureException(e)
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        if (this::appUpdateManager.isInitialized) appUpdateManager.completeUpdate()

      /*  Snackbar.make(
            activity.findViewById<View>(android.R.id.content).rootView,
            fetchString(R.string.download_complete), Snackbar.LENGTH_INDEFINITE
        ).setAction(fetchString(R.string.install)) {
            if (this::appUpdateManager.isInitialized) appUpdateManager.completeUpdate()
        }.setActionTextColor(ContextCompat.getColor(activity, R.color.green_light)).show()*/
    }

    fun removeInstallStateUpdateListener() {
        if (this::appUpdateManager.isInitialized) appUpdateManager.unregisterListener(appUpdateListener)
    }


}