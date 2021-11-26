package com.appservice.rest.services

import com.appservice.model.domainBooking.request.ExistingDomainRequest
import com.appservice.rest.EndPoints
import okhttp3.ResponseBody
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface RiaNowFloatsRemoteData {

  @POST(EndPoints.ADD_EXISTING_DOMAIN_DETAILS)
  fun addExistingDomainDetails(
    @Query("authClientId") clientId: String?,
    @Query("fpTag") fpTag: String?,
    @Body bodyRequest: ExistingDomainRequest?,
  ): Observable<Response<ResponseBody>>

}