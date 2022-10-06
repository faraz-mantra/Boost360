package com.festive.poster.reset.repo

import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.models.CustomerDetails
import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.models.promoModele.TagListRequest
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.WithFloatsTwoApiClient
import com.festive.poster.reset.services.WithFloatTwoRemoteData
import com.framework.base.BaseResponse
import com.framework.pref.clientId
import com.google.gson.JsonObject
import com.squareup.okhttp.RequestBody
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import java.util.HashMap

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

  fun putBizImageUpdateV2(
    type: String?,
    bizMessageId: String?,
    imageBase64: String?,
    sendToSubscribers: Boolean?,
    socialParmeters: String?
  ): Observable<BaseResponse> {
    val jsonObject = JsonObject()
    jsonObject.addProperty("type",type)
    jsonObject.addProperty("clientId", clientId)
    jsonObject.addProperty("socialParmeters", socialParmeters)
    jsonObject.addProperty("sendToSubscribers", sendToSubscribers)
    jsonObject.addProperty("bizMessageId", bizMessageId)
    jsonObject.addProperty("imageBody",imageBase64)
    return makeRemoteRequest(
      remoteDataSource.putBizImageUpdateV2(
        jsonObject
      ), TaskCode.PUT_IMAGE_BIZ_UPDATE_V2
    )
  }
  fun putBizMessageUpdateV2(request: PostUpdateTaskRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.putBizMessageUpdateV2(request),
      TaskCode.PUT_BIZ_MESSAGE_UPDATEV2
    )
  }
  fun getUserDetails(): Observable<BaseResponse> {
    val queries: MutableMap<String, String> = HashMap()
    queries["clientId"] = clientId
    return makeRemoteRequest(
      remoteDataSource.getUserDetails(session.fpTag,queries),
      TaskCode.GET_CUSTOMER_DETAILS
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
