package com.festive.poster.reset.services

import com.festive.poster.models.PosterModel
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.reset.EndPoints
import com.framework.base.BaseResponse
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
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
    ): Observable<Response<GetTemplateViewConfigResponse>>

  @POST(EndPoints.GET_TEMPLATES)
  fun getTemplates(
    @Body body:JsonObject?,
    ): Observable<Response<GetTemplatesResponse>>

  @POST(EndPoints.UPLOAD_IMAGE)
  fun uploadImage(
    @Query("floatingPointId") floatingPointId:String?,
    @Query("tag") tag:String?,
    @Query("fileName") fileName:String?,
    @Body file: RequestBody?
  ): Observable<Response<String>>

  @POST(EndPoints.SAVE_KEY_VALUE)
  fun saveKeyValue(
    @Body body:JsonObject?,
  ): Observable<Response<BaseResponse>>

}