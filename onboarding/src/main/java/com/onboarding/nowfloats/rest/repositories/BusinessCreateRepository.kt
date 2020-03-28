package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.services.remote.business.BusinessCreateRemoteDataSource
import io.reactivex.Observable

object BusinessCreateRepository : AppBaseRepository<BusinessCreateRemoteDataSource, AppBaseLocalService>() {

    override fun getRemoteDataSourceClass(): Class<BusinessCreateRemoteDataSource> {
        return BusinessCreateRemoteDataSource::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    fun postCreateBusinessOnboarding(request: BusinessCreateRequest): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.createBusinessOnboarding(request = request), Taskcode.POST_CREATE_BUSINESS_ONBOARDING)
    }
}