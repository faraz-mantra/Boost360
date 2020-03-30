package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.WithFloatsApiClient
import com.onboarding.nowfloats.rest.services.remote.business.BusinessCreateRemoteDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object BusinessCreateRepository : AppBaseRepository<BusinessCreateRemoteDataSource, AppBaseLocalService>() {

    override fun getRemoteDataSourceClass(): Class<BusinessCreateRemoteDataSource> {
        return BusinessCreateRemoteDataSource::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    fun postCreateBusinessOnboarding(profileId: String?, request: BusinessCreateRequest): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.createBusinessOnboarding(profileId, request), Taskcode.POST_CREATE_BUSINESS_ONBOARDING)
    }

    override fun getApiClient(): Retrofit {
        return WithFloatsApiClient.shared.retrofit
    }
}