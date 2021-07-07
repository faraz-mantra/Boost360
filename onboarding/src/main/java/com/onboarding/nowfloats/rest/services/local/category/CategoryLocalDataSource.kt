package com.onboarding.nowfloats.rest.services.local.category

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.model.plan.Plan15DaysResponse
import com.onboarding.nowfloats.model.plan.Plan15DaysResponseItem
import com.onboarding.nowfloats.rest.response.category.ResponseDataCategory
import io.reactivex.Observable

object CategoryLocalDataSource : AppBaseLocalService() {

  fun getCategory(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.category_data_model_v3, ResponseDataCategory::class.java)
  }

  fun getCategoryPlan(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.plans, Plan15DaysResponse::class.java)
  }
}