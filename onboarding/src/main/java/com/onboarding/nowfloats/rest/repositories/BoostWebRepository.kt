package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.BoostFloatClient
import com.onboarding.nowfloats.rest.services.remote.boostweb.BoostWebDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object BoostWebRepository : AppBaseRepository<BoostWebDataSource, AppBaseLocalService>() {

  fun getMerchantProfile(floatingpointId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getMerchantProfile(floatingpointId),
      Taskcode.GET_MERCHANT_PROFILE
    )
  }

  override fun getRemoteDataSourceClass(): Class<BoostWebDataSource> {
    return BoostWebDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return BoostFloatClient.shared.retrofit
  }
}