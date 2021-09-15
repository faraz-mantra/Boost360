package com.appservice.rest.repository

import com.appservice.model.aptsetting.UpdateInfoRequest
import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.BoostNowFloatsApiClient
import com.appservice.rest.services.BoostNowFloatsRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object BoostNowFloatsRepository : AppBaseRepository<BoostNowFloatsRemoteData, AppBaseLocalService>() {


  fun updateInfo(request: UpdateInfoRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateInfo(request),
      TaskCode.UPDATE_CATEGORY_NAME
    )
  }


  override fun getRemoteDataSourceClass(): Class<BoostNowFloatsRemoteData> {
    return BoostNowFloatsRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return BoostNowFloatsApiClient.shared.retrofit
  }
}
