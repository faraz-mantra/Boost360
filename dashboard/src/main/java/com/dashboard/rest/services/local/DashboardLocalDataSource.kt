package com.dashboard.rest.services.local

import android.content.Context
import com.dashboard.R
import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.model.QuickActionDataResponse
import com.dashboard.model.live.addOns.AllBoostAddOnsDataResponse
import com.dashboard.model.live.drawerData.DrawerHomeDataResponse
import com.framework.base.BaseResponse
import io.reactivex.Observable


object DashboardLocalDataSource : AppBaseLocalService() {

  fun getBoostAddOns(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.boost_add_ons, AllBoostAddOnsDataResponse::class.java)
  }

  fun getNavDashboardData(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.nav_dashboard_data, DrawerHomeDataResponse::class.java)
  }

  fun getQuickActionData(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.quick_action_data, QuickActionDataResponse::class.java)
  }

}