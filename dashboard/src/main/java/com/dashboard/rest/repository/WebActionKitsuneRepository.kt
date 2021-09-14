package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseLocalService
import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.rest.TaskCode.UPLOAD_OWNERS_PROFILE
import com.dashboard.rest.apiClients.WebActionKitSuneClientN
import com.dashboard.rest.services.WebActionKitsuneRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit

object WebActionKitsuneRepository : AppBaseRepository<WebActionKitsuneRemoteData, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<WebActionKitsuneRemoteData> {
        return WebActionKitsuneRemoteData::class.java
    }

    fun uploadOwnersProfileImage(auth: String?, assetFileName: String?, file: MultipartBody.Part?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.uploadOwnersProfileImage(auth, assetFileName, file), UPLOAD_OWNERS_PROFILE)
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getApiClient(): Retrofit {
        return WebActionKitSuneClientN.shared.retrofit
    }
}