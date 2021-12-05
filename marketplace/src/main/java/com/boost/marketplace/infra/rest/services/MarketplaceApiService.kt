package com.boost.marketplace.infra.rest.services

import com.boost.marketplace.infra.api.models.business.model.BusinessProfileUpdateRequest
import com.boost.marketplace.infra.rest.EndPoints
import com.framework.base.BaseResponse
import com.framework.models.UserProfileData
import com.onboarding.nowfloats.model.googleAuth.FirebaseTokenResponse
import com.framework.pref.clientId2
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

interface MarketplaceApiService {

  @PUT(EndPoints.CREATE_BUSINESS_LOGO)
  fun uploadBusinessImage(
    @Query("clientId") clientId: String?,
    @Query("fpId") fpId: String?,
    @Query("reqType") reqType: String?,
    @Query("reqtId") reqtId: String?,
    @Query("totalChunks") totalChunks: String?,
    @Query("currentChunkNumber") currentChunkNumber: String?,
    @Body file: RequestBody?
  ): Observable<Response<ResponseBody>>


  @POST(EndPoints.FLOATING_POINT_UPDATE)
  fun updateBusinessProfile(
    @Body profileUpdateRequest: BusinessProfileUpdateRequest
  ): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_FIREBASE_TOKEN)
  fun getFirebaseToken(
    @Query("clientId") client_id: String?
  ):Observable<Response<FirebaseTokenResponse>>

  @PUT(EndPoints.UPLOAD_USER_PROFILE_IMAGE)
  fun uploadUserProfileImage(
    @Query("clientId") clientId: String?,
    @Query("loginId") loginId: String?,
    @Query("fileName") fileName: String?,
    @Body file: RequestBody?
  ): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_USER_PROFILE_DETAILS+"/{loginId}")
  fun userProfileData(
    @Path("loginId") loginId: String?,
    @Query("clientId") clientId: String?= clientId2,
    ): Observable<Response<UserProfileData>>

  @POST(EndPoints.UPDATE_USER_NAME)
  fun updateUserName(
    @Query("clientId") clientId: String?= clientId2,
    @Body jsonObject: JsonObject
  ): Observable<Response<BaseResponse>>

  @GET(EndPoints.SEND_OTP_EMAIL)
  fun sendOTPEmail(
    @Query("emailId") emailId: String?,
    @Query("clientId") clientId: String?= clientId2,
  ): Observable<Response<ResponseBody>>

  @POST(EndPoints.UPDATE_EMAIL)
  fun updateEmail(
    @Query("clientId") clientId: String?= clientId2,
    @Body jsonObject: JsonObject
  ): Observable<Response<BaseResponse>>

  @GET(EndPoints.SEND_OTP_MOBILE)
  fun sendOTPMobile(
    @Query("mobileNumber") mobileNumber: String?,
    @Query("messageTemplate") messageTemplate: String? = "Your one time Boost 360 verification code is [OTP]. The code is valid for 10 minutes, Please DO NOT share this code with anyone.#W5izmPg6WcR",
    @Query("clientId") clientId: String?= clientId2,
  ): Observable<Response<ResponseBody>>

  @POST(EndPoints.UPDATE_MOBILE)
  fun updateMobile(
    @Query("clientId") clientId: String?= clientId2,
    @Body jsonObject: JsonObject
  ): Observable<Response<BaseResponse>>

  @POST(EndPoints.UPDATE_WHATSAPP)
  fun updateWhatsapp(
    @Query("clientId") clientId: String?= clientId2,
    @Body jsonObject: JsonObject
  ): Observable<Response<BaseResponse>>
}