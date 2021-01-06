package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.StaffWithFloatsApiClient
import com.appservice.rest.services.StaffWithFloatsRemoteData
import com.appservice.staffs.model.StaffCreateProfileRequest
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object StaffWithFloatsRepository : AppBaseRepository<StaffWithFloatsRemoteData, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<StaffWithFloatsRemoteData> {
        return StaffWithFloatsRemoteData::class.java
    }

    fun createProfile(request: StaffCreateProfileRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(StaffWithFloatsRepository.remoteDataSource.createStaffProfile(request), TaskCode.CREATE_STAFF_PROFILE)
    }

    override fun getApiClient(): Retrofit {
        return StaffWithFloatsApiClient.shared.retrofit
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }
}