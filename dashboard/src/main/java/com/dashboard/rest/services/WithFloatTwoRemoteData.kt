package com.dashboard.rest.services

import com.dashboard.controller.ui.business.model.BusinessProfileUpdateRequest
import com.dashboard.model.live.user_profile.UserProfileData
import com.dashboard.rest.EndPoints
import com.framework.base.BaseResponse
import com.framework.pref.clientId2
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

interface WithFloatTwoRemoteData {
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

  @PUT(EndPoints.UPLOAD_USER_PROFILE_IMAGE)
  fun uploadUserProfileImage(
    @Query("clientId") clientId: String?,
    @Query("loginId") loginId: String?,
    @Query("fileName") fileName: String?,
    @Body file: RequestBody?
  ): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_USER_PROFILE_DETAILS+"/{loginId}")
  fun useProfileData(
    @Path("loginId") loginId: String?,
    @Query("clientId") clientId: String?= clientId2,
    ): Observable<Response<UserProfileData>>
}