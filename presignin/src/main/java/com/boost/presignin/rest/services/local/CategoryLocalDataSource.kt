package com.boost.presignin.rest.services.local

import android.content.Context
import com.boost.presignin.R
import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.model.plan.Plan15DaysResponse
import com.boost.presignin.rest.response.ResponseDataCategory
import com.framework.base.BaseResponse
import com.boost.presignin.model.plan.Plan15DaysResponseItem
import io.reactivex.Observable

object CategoryLocalDataSource : AppBaseLocalService() {

  fun getCategory(context: Context): Observable<BaseResponse> {
      return fromJsonRes(context, R.raw.category_data_model_v3, ResponseDataCategory::class.java)
  }

  fun getCategoryPlan(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.plans, Plan15DaysResponse::class.java)
  }
}