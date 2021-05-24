package com.onboarding.nowfloats.rest.services.remote.google

import com.onboarding.nowfloats.model.googleAuth.account.AccountData
import com.onboarding.nowfloats.model.googleAuth.account.AccountListResponse
import com.onboarding.nowfloats.rest.EndPoints
import com.onboarding.nowfloats.rest.response.AccountLocationResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

//google Api not remove Authorization

interface GMBRemoteDataSource {

  @GET(EndPoints.GET_GMB_ACCOUNT_LOCATIONS)
  fun getAccountLocations(@Header("Authorization") auth: String?, @Path("user_id") user_id: String?): Observable<Response<AccountLocationResponse>>

  @GET(EndPoints.GET_GMB_ACCOUNT)
  fun getAccount(@Header("Authorization") auth: String?, @Path("user_id") user_id: String?): Observable<Response<AccountData>>

  @GET(EndPoints.GET_GMB_ACCOUNT_LIST)
  fun getAccountList(@Header("Authorization") auth: String?): Observable<Response<AccountListResponse>>
}