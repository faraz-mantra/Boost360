package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
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

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    fun putCreateBusinessOnboarding(profileId: String?, request: BusinessCreateRequest): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.putCreateBusinessOnboarding(profileId, request), TaskCode.POST_CREATE_BUSINESS_ONBOARDING)
    }


    override fun getApiClient(): Retrofit {
        return WithFloatsApiClient.shared.retrofit
    }
}