package com.onboarding.nowfloats.rest.services.local.feature

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.rest.response.feature.FeatureListResponse
import io.reactivex.Observable


object FeatureLocalDataSource : AppBaseLocalService() {

  fun getFeatures(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.features, FeatureListResponse::class.java)
  }
}