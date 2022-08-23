package com.appservice.rest.services

import com.appservice.model.account.AccountCreateRequest
import com.appservice.model.account.BankAccountDetailsN
import com.appservice.model.account.response.AccountCreateResponse
import com.appservice.model.accountDetails.AccountDetailsResponse
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface WithFloatRemoteData {

  @GET(EndPoints.USER_ACCOUNT_DETAIL)
  fun userAccountDetail(
    @Path("fpId") fpId: String?,
    @Query("clientId") clientId: String?
  ): Observable<Response<AccountDetailsResponse>>

  @POST(EndPoints.CREATE_PAYMENT)
  fun createAccount(@Body request: AccountCreateRequest?): Observable<Response<AccountCreateResponse>>

  @PUT(EndPoints.UPDATE_PAYMENT)
  fun updateAccount(
    @Path("fpId") fpId: String?,
    @Query("clientId") clientId: String?,
    @Body request: BankAccountDetailsN?
  ): Observable<Response<AccountCreateResponse>>

  @PUT(com.onboarding.nowfloats.rest.EndPoints.PUT_UPLOAD_CREATE_IMAGE)
  fun putUploadImage(
    @Query("clientId") clientId: String?,
    @Query("fpId") fpId: String?,
    @Query("identifierType") identifierType: String? = "SINGLE",
    @Query("reqType") reqType: String? = "sequential",
    @Query("reqtId") reqtId: String? = UUID.randomUUID().toString().replace("-", ""),
    @Query("totalChunks") totalChunks: String? = "1",
    @Query("currentChunkNumber") currentChunkNumber: String? = "1",
    @Query("fileName") fileName: String?,
    @Body requestBody: RequestBody?,
  ): Observable<Response<String>>


}