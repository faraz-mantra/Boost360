package com.framework.utils

import android.app.Activity
import com.framework.BaseApplication
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode

object InAppReviewUtils {

    enum class Events{
        in_app_review_website_preview_cross,
        in_app_review_third_update,
        in_app_review_out_of_customer_messages,
        in_app_review_out_of_customer_calls,
        in_app_review_out_of_customer_orders,
        in_app_review_rate_us_on_store,
        in_app_review_first_promo_update,
        in_app_review_first_purchase


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