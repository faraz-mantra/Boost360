package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.API2WithFloatsApiClient
import com.dashboard.rest.services.API2WithFloatsRemoteData
import com.framework.base.BaseResponse
import com.framework.pref.clientId
import io.reactivex.Observable
import retrofit2.Retrofit

object API2WithFloatsRepository : AppBaseRepository<API2WithFloatsRemoteData, AppBaseLocalService>() {

    override fun getRemoteDataSourceClass(): Class<API2WithFloatsRemoteData> {
        return API2WithFloatsRemoteData::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return API2WithFloatsApiClient.shared.retrofit
    }

    fun republishWebsite(fpTag: String): Observable<BaseResponse> {
        return API2WithFloatsRepository.makeRemoteRequest(
            API2WithFloatsRepository.remoteDataSource.republishWebsite(clientId = clientId, fpTag = fpTag), TaskCode.REPUBLISH_WEBSITE)
    }
}