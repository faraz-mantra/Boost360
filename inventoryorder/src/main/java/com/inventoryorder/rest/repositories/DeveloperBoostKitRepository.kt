package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.DeveloperBoostKitClient
import com.inventoryorder.rest.services.DeveloperBoostKitDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object DeveloperBoostKitRepository : AppBaseRepository<DeveloperBoostKitDataSource, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<DeveloperBoostKitDataSource> {
        return DeveloperBoostKitDataSource::class.java
    }
    fun getTutorialsData(website:String?,auth: String?): Observable<BaseResponse> {
        return DeveloperBoostKitRepository.makeRemoteRequest(DeveloperBoostKitRepository.remoteDataSource.getTutorialsData(website = website, auth = auth), TaskCode.GET_TUTORIALS_DATA)
    }
    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return DeveloperBoostKitClient.shared.retrofit
    }
}