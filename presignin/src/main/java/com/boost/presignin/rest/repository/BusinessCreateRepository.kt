package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.model.activatepurchase.ActivatePurchasedOrderRequest
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.apiClients.WithFloatsApiClient
import com.boost.presignin.rest.services.remote.BusinessCreateRemoteDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object BusinessCreateRepository : AppBaseRepository<BusinessCreateRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<BusinessCreateRemoteDataSource> {
    return BusinessCreateRemoteDataSource::class.java
  }

  fun postActivatePurchasedOrder(
    clientId: String?,
    request: ActivatePurchasedOrderRequest
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.postActivatePurchasedOrder(clientId, request),
      TaskCode.POST_ACTIVATE_PURCHASED_ORDER
    )
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun putCreateBusinessV5(
    profileId: String?,
    request: BusinessCreateRequest
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putCreateBusinessV5(profileId, request),
      TaskCode.POST_CREATE_BUSINESS_V5
    )
  }

  fun putCreateBusinessV6(
    profileId: String?,
    request: BusinessCreateRequest
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putCreateBusinessV6(profileId, request),
      TaskCode.POST_CREATE_BUSINESS_V6
    )
  }


  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}