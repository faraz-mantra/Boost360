package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.LogotronBuildMyLogoApiClient
import com.appservice.rest.services.LogotronBuildMyLogoRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object LogotronBuildMyLogoRepository :
    AppBaseRepository<LogotronBuildMyLogoRemoteData, AppBaseLocalService>() {


    fun addLogotronClient(firstName: String?, lastName: String?, email: String?, phoneNumber: String?): Observable<BaseResponse> {
        return LogotronBuildMyLogoRepository.makeRemoteRequest(
            remoteDataSource.addLogotronClient(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber),
            TaskCode.ADD_LOGOTRON_CLIENT
        )
    }

    fun downloadLogotronImage(): Observable<BaseResponse> {
        return LogotronBuildMyLogoRepository.makeRemoteRequest(
            remoteDataSource.downloadLogotronImage(),
            TaskCode.DOWNLOAD_LOGOTRON_IMAGE
        )
    }

    override fun getRemoteDataSourceClass(): Class<LogotronBuildMyLogoRemoteData> {
        return LogotronBuildMyLogoRemoteData::class.java
    }

    override fun getApiClient(): Retrofit {
        return LogotronBuildMyLogoApiClient.shared.retrofit
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }
}