package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.domain.BusinessDomainRequest
import com.onboarding.nowfloats.model.domain.BusinessDomainSuggestRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.WithFloatsApiClient
import com.onboarding.nowfloats.rest.services.remote.domain.BusinessDomainRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object BusinessDomainRepository :
  AppBaseRepository<BusinessDomainRemoteDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<BusinessDomainRemoteDataSource> {
    return BusinessDomainRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun postCheckBusinessDomain(request: BusinessDomainRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.checkBusinessDomain(request),
      Taskcode.POST_CHECK_BUSINESS_DOMAIN
    )
  }

  fun postCheckBusinessDomainSuggest(request: BusinessDomainSuggestRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.checkBusinessDomainSuggest(request),
      Taskcode.POST_CHECK_BUSINESS_DOMAIN_SUGGEST
    )
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsApiClient.shared.retrofit
  }
}