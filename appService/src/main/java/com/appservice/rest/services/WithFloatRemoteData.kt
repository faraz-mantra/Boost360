package com.appservice.rest.services

import com.appservice.model.account.AccountCreateRequest
import com.appservice.model.account.BankAccountDetailsN
import com.appservice.model.account.response.AccountCreateResponse
import com.appservice.model.accountDetails.AccountDetailsResponse
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

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
}