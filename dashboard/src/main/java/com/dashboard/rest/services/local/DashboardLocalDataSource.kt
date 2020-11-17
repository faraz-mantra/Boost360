package com.dashboard.rest.services.local

import android.content.Context
import com.dashboard.R
import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.model.live.addOns.AllBoostAddOnsDataResponse
import com.framework.base.BaseResponse
import io.reactivex.Observable


object DashboardLocalDataSource : AppBaseLocalService() {

  fun getBoostAddOns(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.boost_add_ons, AllBoostAddOnsDataResponse::class.java)
  }
}