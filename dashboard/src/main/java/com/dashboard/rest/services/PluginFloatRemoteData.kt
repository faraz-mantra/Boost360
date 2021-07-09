package com.dashboard.rest.services

import com.dashboard.model.live.domainDetail.DomainDetailResponse
import com.dashboard.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PluginFloatRemoteData {

  @GET(EndPoints.DOMAIN_DETAIL)
  fun getDomainDetailsForFloatingPoint(
      @Path("fpTag") fpTag: String?,
      @QueryMap map: Map<String, String>?
  ): Observable<Response<DomainDetailResponse>>
}