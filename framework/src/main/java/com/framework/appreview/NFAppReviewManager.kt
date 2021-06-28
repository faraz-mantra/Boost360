package com.framework.appreview

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

object NFAppReviewManager {
    const val TAG = "NFAppReviewManager"
    fun requestReview(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                launchFlow(manager, activity, reviewInfo)
            } else {
                task.exception?.message?.let { Log.e(TAG, it) }
                // There was some problem, log or handle the error code.
            }
        }
    }

    private fun launchFlow(manager: ReviewManager, activity: Activity, reviewInfo: ReviewInfo) {
        val flow = manager.launchReviewFlow(activity, reviewInfo)
        flow.addOnCompleteListener { _ ->
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
        }
    }
}