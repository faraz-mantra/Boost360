package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.BoostPluginWithFloatsApiClient
import com.appservice.rest.services.BoostPluginWithFloatsRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object BoostPluginWithFloatsRepository : AppBaseRepository<BoostPluginWithFloatsRemoteData, AppBaseLocalService>() {

    override fun getRemoteDataSourceClass(): Class<BoostPluginWithFloatsRemoteData> {
        return BoostPluginWithFloatsRemoteData::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return BoostPluginWithFloatsApiClient.shared.retrofit
    }

    fun domainDetails(fpId: String?, clientId: String?): Observable<BaseResponse> {
        return makeRemoteRequest(
            remoteDataSource.getDomainDetails(fpId, clientId),
            TaskCode.DOMAIN_DETAILS
        )
    }
}