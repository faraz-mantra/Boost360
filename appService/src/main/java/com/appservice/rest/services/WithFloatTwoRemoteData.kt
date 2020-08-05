package com.appservice.rest.services

import com.appservice.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface WithFloatTwoRemoteData {

  @GET(EndPoints.CREATE_SERVICE)
  fun createService(@Body request: Any): Observable<Response<Any>>
}