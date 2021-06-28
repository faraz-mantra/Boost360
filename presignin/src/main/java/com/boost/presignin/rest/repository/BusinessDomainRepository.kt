package com.boost.presignin.rest.repository

import BusinessDomainRequest
import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.model.businessdomain.BusinessDomainSuggestRequest
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.apiClients.WithFloatsApiClient
import com.boost.presignin.rest.services.remote.BusinessDomainRemoteDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object BusinessDomainRepository: AppBaseRepository<BusinessDomainRemoteDataSource, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<BusinessDomainRemoteDataSource> {
        return BusinessDomainRemoteDataSource::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    fun postCheckBusinessDomain(request: BusinessDomainRequest): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.checkBusinessDomain(request), TaskCode.POST_CHECK_BUSINESS_DOMAIN)
    }
    override fun getApiClient(): Retrofit {
        return WithFloatsApiClient.shared.retrofit
    }
}