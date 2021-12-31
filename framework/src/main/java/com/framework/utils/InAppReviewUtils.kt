package com.framework.utils

import android.app.Activity
import com.framework.BaseApplication
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode

object InAppReviewUtils {

    enum class Events{
        WEBSITE_PREVIEW_CROSS,
        THIRD_UPDATE,
        OUT_OF_CUSTOMER_MESSAGES,
        OUT_OF_CUSTOMER_CALLS,
        OUT_OF_CUSTOMER_ORDERS,
        RATE_US_ON_STORE,
        FIRST_PROMO_UPDATE,
        FIRST_PURCHASE


    }

    fun showInAppReview(activity:Activity,event: Events){
        if (FirebaseRemoteConfigUtil.isInAppReviewFlagEnabled(event)){
            val manager = ReviewManagerFactory.create(activity)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(activity, reviewInfo)

                } else {
                    // There was some problem, log or handle the error code.
                }
            }
        }

    }
}