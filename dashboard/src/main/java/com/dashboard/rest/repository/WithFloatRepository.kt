package com.dashboard.rest.repository

import android.content.Context
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.WithFloatsApiClient
import com.dashboard.rest.services.WithFloatRemoteData
import com.dashboard.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit


object WithFloatRepository : AppBaseRepository<WithFloatRemoteData, DashboardLocalDataSource>() {

  fun getBoostAddOns(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getBoostAddOns(context),
      TaskCode.GET_BOOST_ADD_ONS
    )
  }

  fun getBoostAddOnsTop(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getBoostAddOnsTop(context),
      TaskCode.GET_BOOST_ADD_ONS_TOP
    )
  }

  fun getWelcomeDashboardData(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getWelcomeDashboardData(context),
      TaskCode.GET_WELCOME_DASHBOARD
    )
  }

  fun getBoostVisitingMessage(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getBoostVisitingMessage(context),
      TaskCode.GET_USER_DETAIL_SHARE_DATA
    )
  }

  fun getBoostCustomerItem(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getBoostCustomerItem(context),
      TaskCode.GET_CUSTOMER_ITEM_DATA
    )
  }

  fun getBoostWebsiteItem(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getBoostWebsiteItem(context),
      TaskCode.GET_WEBSITE_ITEM_DATA
    )
  }

  fun getNavDashboardData(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getNavDashboardData(context),
      TaskCode.GET_NAV_DASHBOARD_DATA
    )
  }
  fun getMoreSettings(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getMoreSettings(context),
      TaskCode.MORE_SETTINGS
    )
  }
  fun getWebsiteNavData(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getWebsiteNavData(context),
      TaskCode.WEBSITE_NAV_DATA
    )
  }


  fun getDrScoreUi(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getDrScoreUi(context),
      TaskCode.GET_DR_SCORE_UI_DATA
    )
  }

  fun getQuickActionData(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(
      DashboardLocalDataSource.getQuickActionData(context),
      TaskCode.GET_QUICK_ACTION_DATA
    )
  }



  override fun getRemoteDataSourceClass(): Class<WithFloatRemoteData> {
    return WithFloatRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): DashboardLocalDataSource {
    return DashboardLocalDataSource
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}
