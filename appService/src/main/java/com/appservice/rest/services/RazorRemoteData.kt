package com.appservice.rest.services

import com.appservice.model.razor.RazorDataResponse
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RazorRemoteData {

  @GET(EndPoints.RAZOR_IFSC_DETAIL)
  fun ifscDetail(@Path("ifsc") ifsc: String?): Observable<Response<RazorDataResponse>>

}