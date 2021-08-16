package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.model.business.purchasedOrder.ActivatePurchasedOrderRequest
import com.onboarding.nowfloats.model.verification.RequestValidateEmail
import com.onboarding.nowfloats.model.verification.RequestValidatePhone
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.WithFloatsApiClient
import com.onboarding.nowfloats.rest.services.remote.business.BusinessCreateRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object BusinessCreateRepository :
  AppBaseRepository<BusinessCreateRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<BusinessCreateRemoteDataSource> {
    return BusinessCreateRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun putCreateBusinessOnboarding(
    profileId: String?,
    request: BusinessCreateRequest
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putCreateBusinessOnboarding(profileId, request),
      Taskcode.POST_CREATE_BUSINESS_ONBOARDING
    )
  }

  fun postActivatePurchasedOrder(
    clientId: String?,
    request: ActivatePurchasedOrderRequest
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.postActivatePurchasedOrder(clientId, request),
      Taskcode.POST_ACTIVATE_PURCHASED_ORDER
    )
  }

  fun validateUsersPhone(requestValidatePhone: RequestValidatePhone?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.validateUserPhone(requestValidatePhone),
      Taskcode.VALIDATE_USERS_PHONE
    )
  }

  fun validateUsersEmail(requestValidateEmail: RequestValidateEmail?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.validateUserEmail(requestValidateEmail),
      Taskcode.VALIDATE_USERS_EMAIL
    )
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}