package com.dashboard.rest.repository

import com.dashboard.base.rest.AppBaseRepository
import com.dashboard.controller.ui.business.model.BusinessProfileUpdateRequest
import com.dashboard.rest.TaskCode
import com.dashboard.rest.apiClients.WithFloatsTwoApiClient
import com.dashboard.rest.services.WithFloatTwoRemoteData
import com.dashboard.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import java.io.File

object WithFloatTwoRepository : AppBaseRepository<WithFloatTwoRemoteData, DashboardLocalDataSource>() {

  fun uploadBusinessLogo(
    clientId: String?, fpId: String?, reqType: String?, reqId: String?,
    totalChunks: String?, currentChunkNumber: String?, file: RequestBody?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.uploadBusinessImage(
        clientId = clientId, fpId = fpId, reqType = reqType,
        reqtId = reqId, totalChunks = totalChunks, currentChunkNumber = currentChunkNumber, file = file
      ), TaskCode.UPLOAD_BUSINESS_IMAGE
    )
  }
  fun updateBusinessProfile(
     profileUpdateRequest: BusinessProfileUpdateRequest
  ): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.
    updateBusinessProfile(profileUpdateRequest = profileUpdateRequest),TaskCode.UPADTE_BUSINESS_PROFILE)
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
