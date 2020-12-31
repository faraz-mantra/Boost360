package com.dashboard.rest.services.local

import android.content.Context
import com.dashboard.R
import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.model.live.addOns.ManageAddOnsBusinessResponse
import com.dashboard.model.live.addOns.ManageBusinessDataResponse
import com.dashboard.model.live.customerItem.BoostCustomerItemResponse
import com.dashboard.model.live.drawerData.DrawerHomeDataResponse
import com.dashboard.model.live.quickAction.QuickActionResponse
import com.dashboard.model.live.shareUser.ShareUserDetailResponse
import com.framework.base.BaseResponse
import io.reactivex.Observable

object DashboardLocalDataSource : AppBaseLocalService() {

  fun getBoostAddOns(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.boost_add_ons, ManageAddOnsBusinessResponse::class.java)
  }

  fun getBoostAddOnsTop(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.boost_add_ons_top, ManageBusinessDataResponse::class.java)
  }

  fun getBoostUserDetailMessage(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.user_details_share, ShareUserDetailResponse::class.java)
  }

  fun getBoostCustomerItem(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.boost_cutomer_item, BoostCustomerItemResponse::class.java)
  }
  fun getNavDashboardData(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.nav_dashboard_data, DrawerHomeDataResponse::class.java)
  }

  fun getQuickActionData(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.quick_action_data, QuickActionResponse::class.java)
  }
}