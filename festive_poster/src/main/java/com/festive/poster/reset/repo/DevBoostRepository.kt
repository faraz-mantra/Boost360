package com.festive.poster.reset.repo



import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.DevBoostKitApiClient
import com.festive.poster.reset.apiClients.NowFloatsApiClient
import com.festive.poster.reset.services.DevBoostRemoteData
import com.festive.poster.reset.services.NowFloatsRemoteData
import com.framework.base.BaseResponse
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.http.Query


object DevBoostRepository : AppBaseRepository<DevBoostRemoteData, AppBaseLocalService>() {


  fun getUpgradeData(): Observable<BaseResponse> {

    return DevBoostRepository.makeRemoteRequest(
      remoteDataSource.getUpgradeData(),
      TaskCode.GET_TEMPLATE_CONFIG
    )
  }

  override fun getRemoteDataSourceClass(): Class<DevBoostRemoteData> {
    return DevBoostRemoteData::class.java
  }



  override fun getApiClient(): Retrofit {
    return DevBoostKitApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}
