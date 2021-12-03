package com.appservice.rest.services

import com.appservice.model.kycData.PaymentKycDataResponse
import com.appservice.model.kycData.kycList.PaymentKycListResponse
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.appservice.model.paymentKyc.update.UpdatePaymentKycRequest
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WebActionBoostKitRemoteData {

  //"query"={"fpTag": "nik(FpTag)"}
  @Headers("X-Auth-Version: 2")
  @GET(EndPoints.GET_DATA_KYC)
  fun getKycData(
    @Header("X-User-Id") auth: String?,
    @Query("query") query: String?,
  ): Observable<Response<PaymentKycDataResponse>>

  @Headers("X-Auth-Version: 2")
  @GET(EndPoints.GET_ALL_KYC_DATA)
  fun getKycListData(@Header("X-User-Id") auth: String?): Observable<Response<PaymentKycListResponse>>

  @Headers("X-Auth-Version: 2")
  @POST(EndPoints.ADD_DATA_KYC)
  fun addKycData(
    @Header("X-User-Id") auth: String?,
    @Body request: PaymentKycRequest?,
  ): Observable<Response<ResponseBody>>

  @Headers("X-Auth-Version: 2")
  @POST(EndPoints.UPDATE_DATA_KYC)
  fun updateKycData(
    @Header("X-User-Id") auth: String?,
    @Body request: UpdatePaymentKycRequest,
  ): Observable<Response<ResponseBody>>

  @Multipart
  @Headers("X-Auth-Version: 2")
  @POST(EndPoints.UPLOAD_FILE)
  fun putUploadImageProfile(
    @Header("X-User-Id") auth: String?,
    @Part file: MultipartBody.Part?,
    @Query("assetFileName") assetFileName: String?,
  ): Observable<Response<ResponseBody>>
}