package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.model.RequestAddOwnersInfo
import com.dashboard.model.UpdateOwnersDataRequest
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.WebActionApiBoostKitClientN
import com.dashboard.rest.services.WebActionBoostKitRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object WebActionBoostKitRepository : AppBaseRepository<WebActionBoostKitRemoteData, AppBaseLocalService>() {

    override fun getRemoteDataSourceClass(): Class<WebActionBoostKitRemoteData> {
        return WebActionBoostKitRemoteData::class.java
    }

    fun addOwnersDataPost(auth: String?, body: RequestAddOwnersInfo?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.addOwnersDataPost(auth, body), TaskCode.ADD_OWNERS_DATA)

    }

    fun updateOwnersDataPost(auth: String?, body: UpdateOwnersDataRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.updateOwnersDataPost(auth, body), TaskCode.UPDATE_OWNERS_DATA)

    }

    fun getOwnersDataPost(auth: String?, query: String?, limit: Int?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.getOwnersDataPost(auth, query, limit), TaskCode.GET_OWNERS_DATA)

    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return WebActionApiBoostKitClientN.shared.retrofit
    }
}