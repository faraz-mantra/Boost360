package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.apiClients.BoostKitDevApiClient
import com.boost.presignin.rest.services.remote.BoostKitDevRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object BoostKitDevRepository : AppBaseRepository<BoostKitDevRemoteData, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<BoostKitDevRemoteData> {
    return BoostKitDevRemoteData::class.java
  }


  fun getCategories(): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getCategories(), TaskCode.GET_CATEGORIES_API)
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return BoostKitDevApiClient.shared.retrofit
  }
}
