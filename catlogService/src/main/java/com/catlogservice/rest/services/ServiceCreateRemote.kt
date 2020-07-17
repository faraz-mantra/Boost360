package com.catlogservice.rest.services

import com.catlogservice.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface ServiceCreateRemote {

  @GET(EndPoints.CREATE_SERVICE)
  fun createService(@Body request: Any): Observable<Response<Any>>
}