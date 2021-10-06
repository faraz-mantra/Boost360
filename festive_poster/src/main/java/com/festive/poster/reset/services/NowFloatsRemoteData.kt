package com.festive.poster.reset.services

import com.festive.poster.reset.EndPoints
import com.framework.base.BaseResponse
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NowFloatsRemoteData {

  @POST(EndPoints.TEMPLATE_VIEW_CONFIG)
  fun getTemplateViewConfig(
    @Body body:JsonObject?,
    ): Observable<Response<BaseResponse>>

  @POST(EndPoints.GET_TEMPLATES)
  fun getTemplates(
    @Body body:JsonObject?,
    ): Observable<Response<BaseResponse>>
}