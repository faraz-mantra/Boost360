package com.onboarding.nowfloats.rest.services.remote.google

import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GMBRemoteDataSource {

  @GET(EndPoints.GET_GMB_ACCOUNT_LIST)
  fun getAccountList(
      @Header("Authorization") auth: String?,
      @Path("user_id") user_id: String?
  ): Observable<Response<Any>>

}