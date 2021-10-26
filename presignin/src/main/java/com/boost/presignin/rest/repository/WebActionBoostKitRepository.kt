package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.apiClients.WebActionBoostKitClient
import com.boost.presignin.rest.services.remote.WebActionBoostKitDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object WebActionBoostKitRepository : AppBaseRepository<WebActionBoostKitDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<WebActionBoostKitDataSource> {
    return WebActionBoostKitDataSource::class.java
  }

  fun getSelfBrandedKyc(query: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getSelfBrandedKyc(query=query), TaskCode.GET_SELF_BRANDED_KYC)
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return WebActionBoostKitClient.shared.retrofit
  }
}