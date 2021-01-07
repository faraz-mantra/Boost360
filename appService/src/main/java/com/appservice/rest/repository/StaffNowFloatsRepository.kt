package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.StaffNowFloatsApiClient
import com.appservice.rest.services.StaffNowFloatsRemoteData
import com.appservice.staffs.model.ServiceListRequest
import com.appservice.staffs.model.StaffCreateProfileRequest
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object StaffNowFloatsRepository : AppBaseRepository<StaffNowFloatsRemoteData, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<StaffNowFloatsRemoteData> {
        return StaffNowFloatsRemoteData::class.java
    }

    fun createProfile(request: StaffCreateProfileRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.createStaffProfile(request), TaskCode.CREATE_STAFF_PROFILE)
    }

    override fun getApiClient(): Retrofit {
        return StaffNowFloatsApiClient.shared.retrofit
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    fun getServicesListing(request: ServiceListRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.fetchServices(request), TaskCode.GET_SERVICE_LISTING)
    }
}