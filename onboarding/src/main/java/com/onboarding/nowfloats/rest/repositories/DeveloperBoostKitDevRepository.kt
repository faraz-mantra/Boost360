package com.onboarding.nowfloats.rest.repositories

import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.apiClients.DeveloperBoostKitDevApiClient
import com.onboarding.nowfloats.rest.services.remote.developerBoostKitDev.DeveloperBoostKitDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object DeveloperBoostKitDevRepository : AppBaseRepository<DeveloperBoostKitDataSource, AppBaseLocalService>() {

    override fun getRemoteDataSourceClass(): Class<DeveloperBoostKitDataSource> {
       return DeveloperBoostKitDataSource::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return DeveloperBoostKitDevApiClient.shared.retrofit
    }

    fun getSupportVideos(): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.getSupportVideos(auth = "597ee93f5d64370820a6127c", website = "61278bf6f2e78f0001811865"),
            Taskcode.GET_SUPPORT_VIDEOS
        )
    }
}