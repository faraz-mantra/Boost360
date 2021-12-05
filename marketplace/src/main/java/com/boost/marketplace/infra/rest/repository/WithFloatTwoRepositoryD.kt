package com.boost.marketplace.infra.rest.repository

import com.boost.marketplace.base.rest.AppBaseRepository
import com.boost.marketplace.infra.api.models.business.model.BusinessProfileUpdateRequest
import com.boost.marketplace.infra.rest.TaskCode
import com.boost.marketplace.infra.rest.apiClients.WithFloatsTwoApiClient
import com.boost.marketplace.infra.rest.services.MarketplaceApiService
import com.boost.marketplace.infra.rest.services.local.DashboardLocalDataSource
import com.framework.base.BaseResponse
import com.framework.pref.clientId
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Retrofit

object WithFloatTwoRepositoryD : AppBaseRepository<MarketplaceApiService, DashboardLocalDataSource>() {

  fun uploadBusinessLogo(clientId: String?, fpId: String?, reqType: String?, reqId: String?, totalChunks: String?, currentChunkNumber: String?, file: RequestBody?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.uploadBusinessImage(
        clientId = clientId, fpId = fpId, reqType = reqType, reqtId = reqId,
        totalChunks = totalChunks, currentChunkNumber = currentChunkNumber, file = file
      ), TaskCode.UPLOAD_BUSINESS_IMAGE
    )
  }

  fun updateBusinessProfile(profileUpdateRequest: BusinessProfileUpdateRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateBusinessProfile(profileUpdateRequest = profileUpdateRequest),
      TaskCode.UPDATE_BUSINESS_PROFILE
    )
  }

  fun getFirebaseAuthToken(): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getFirebaseToken(client_id = clientId), TaskCode.GET_FIREBASE_TOKEN)
  }

  fun uploadUserProfileImage(clientId: String?,loginId:String?,fileName:String, file: RequestBody?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.uploadUserProfileImage(
        clientId = clientId, loginId=loginId,
        fileName=fileName, file = file
      ), TaskCode.UPLOAD_USER_PROFILE_IMAGE
    )
  }

  fun getUserProfileData(loginId:String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.userProfileData(loginId=loginId,), TaskCode.GET_USER_PROFILE_DETAILS)
  }

  fun updateUserName(userName:String?, loginId:String?): Observable<BaseResponse> {
    val jsonObject = JsonObject().apply {
      addProperty("UserName",userName)
      addProperty("LoginId",loginId)
    }
    return makeRemoteRequest(remoteDataSource.updateUserName(jsonObject=jsonObject), TaskCode.UPDATE_USER_NAME)
  }

  fun sendOtpEmail(emailId:String?, ): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.sendOTPEmail(emailId), TaskCode.SEND_OTP_EMAIL)
  }

  fun updateEmail(email:String?, otp:String?, loginId:String?): Observable<BaseResponse> {
    val jsonObject = JsonObject().apply {
      addProperty("Email",email)
      addProperty("OTP",otp)
      addProperty("LoginId",loginId)
    }
    return makeRemoteRequest(remoteDataSource.updateEmail(jsonObject=jsonObject), TaskCode.UPDATE_USER_EMAIL)
  }

  fun sendOtpMobile(mobile:String?, ): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.sendOTPMobile(mobile), TaskCode.SEND_OTP_MOBILE)
  }

  fun updateMobile(mobile:String?, otp:String?, loginId:String?): Observable<BaseResponse> {
    val jsonObject = JsonObject().apply {
      addProperty("MobileNo",mobile)
      addProperty("OTP",otp)
      addProperty("LoginId",loginId)
    }
    return makeRemoteRequest(remoteDataSource.updateMobile(jsonObject=jsonObject), TaskCode.UPDATE_USER_MOBILE)
  }

  fun updateWhatsapp(mobile:String?, optIn:Boolean?, loginId:String?): Observable<BaseResponse> {
    val jsonObject = JsonObject().apply {
      addProperty("WhatsappNo",mobile)
      addProperty("OptIn",optIn)
      addProperty("LoginId",loginId)
    }
    return makeRemoteRequest(remoteDataSource.updateWhatsapp(jsonObject=jsonObject), TaskCode.UPDATE_USER_WHATSAPP)
  }

  override fun getRemoteDataSourceClass(): Class<MarketplaceApiService> {
    return MarketplaceApiService::class.java
  }

  override fun getLocalDataSourceInstance(): DashboardLocalDataSource {
    return DashboardLocalDataSource
  }

  override fun getApiClient(): Retrofit {
    return WithFloatsTwoApiClient.shared.retrofit
  }
}
