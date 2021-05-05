package com.dashboard.rest.services

import com.dashboard.model.websitetheme.WebsiteThemeResponse
import com.dashboard.model.websitetheme.WebsiteThemeUpdateRequest
import com.dashboard.rest.EndPoints
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NowFloatsRemoteData {
  @GET(EndPoints.WEBSITE_THEME_GET)
  fun getWebsiteTheme(
      @Query("floatingPointId") floatingPointId: String,
  ): Observable<Response<WebsiteThemeResponse>>

  @POST(EndPoints.WEBSITE_THEME_UPDATE)
  fun updateWebsiteTheme(
      @Body request: WebsiteThemeUpdateRequest,
  ): Observable<Response<ResponseBody>>
}