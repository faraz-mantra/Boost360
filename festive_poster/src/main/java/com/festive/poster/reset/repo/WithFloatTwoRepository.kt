package com.festive.poster.reset.repo

import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.WithFloatsTwoApiClient
import com.festive.poster.reset.services.WithFloatTwoRemoteData
import com.framework.base.BaseResponse
import com.squareup.okhttp.RequestBody
import io.reactivex.Observable
import retrofit2.Retrofit

object WithFloatTwoRepository : AppBaseRepository<WithFloatTwoRemoteData, AppBaseLocalService>() {


  override fun getRemoteDataSourceClass(): Class<WithFloatTwoRemoteData> {
    return WithFloatTwoRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsTwoApiClient.shared.retrofit
  }


  fun putBizMessageUpdate(request: PostUpdateTaskRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putBizMessageUpdate(request),
      TaskCode.PUT_BIZ_MESSAGE_UPDATE
    )
  }


  fun getMerchantSummary(clientId: String?,fpTag:String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getMerchantSummary(clientId,fpTag), TaskCode.GET_MERCHANT_SUMMARY)
  }

  fun putBizImageUpdate(
    clientId: String?,
    requestType: String?,
    requestId: String?,
    totalChunks: Int?,
    currentChunkNumber: Int?,
    socialParmeters: String?,
    bizMessageId: String?,
    sendToSubscribers: Boolean?,
    requestBody: okhttp3.RequestBody?,
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putBizImageUpdate(
        clientId, requestType, requestId, totalChunks, currentChunkNumber,
        socialParmeters, bizMessageId, sendToSubscribers, requestBody
      ), TaskCode.PUT_IMAGE_BIZ_UPDATE
    )
  }


}
