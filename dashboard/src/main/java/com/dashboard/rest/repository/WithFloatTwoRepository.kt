package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.WithFloatsTwoApiClient
import com.dashboard.rest.services.WithFloatTwoRemoteData
import com.dashboard.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File


object WithFloatTwoRepository :
    AppBaseRepository<WithFloatTwoRemoteData, DashboardLocalDataSource>() {

    fun uploadBusinessLogo(
        clientId: String?,
        fpId: String?,
        reqType: String?,
        reqId: String?,
        totalChunks: String?,
        currentChunkNumber: String?,
        file: RequestBody?
    ): Observable<BaseResponse> {
        return makeRemoteRequest(
            observable = remoteDataSource.uploadBusinessImage(
                clientId = clientId,
                fpId = fpId,
                reqType = reqType,
                reqtId = reqId,
                totalChunks = totalChunks,
                currentChunkNumber = currentChunkNumber,
                file = file
            ), taskCode = TaskCode.UPLOAD_BUSINESS_IMAGE
        )
    }

    override fun getRemoteDataSourceClass(): Class<WithFloatTwoRemoteData> {
        return WithFloatTwoRemoteData::class.java
    }

    override fun getLocalDataSourceInstance(): DashboardLocalDataSource {
        return DashboardLocalDataSource
    }

    override fun getApiClient(): Retrofit {
        return WithFloatsTwoApiClient.shared.retrofit
    }
}
