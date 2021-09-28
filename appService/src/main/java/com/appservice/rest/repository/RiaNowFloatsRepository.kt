package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.domainBooking.request.ExistingDomainRequest
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.RiaNowFloatsApiClient
import com.appservice.rest.services.RiaNowFloatsRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object RiaNowFloatsRepository : AppBaseRepository<RiaNowFloatsRemoteData, AppBaseLocalService>() {

    override fun getRemoteDataSourceClass(): Class<RiaNowFloatsRemoteData> {
        return RiaNowFloatsRemoteData::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return RiaNowFloatsApiClient.shared.retrofit
    }

    fun addExistingDomainDetails(fpId: String?, clientId: String?, existingDomainRequest: ExistingDomainRequest): Observable<BaseResponse> {
        return RiaNowFloatsRepository.makeRemoteRequest(
            RiaNowFloatsRepository.remoteDataSource.addExistingDomainDetails(fpId, clientId, existingDomainRequest),
            TaskCode.ADD_EXISTING_DOMAIN_DETAILS
        )
    }
}