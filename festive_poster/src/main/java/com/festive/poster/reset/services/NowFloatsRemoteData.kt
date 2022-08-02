package com.festive.poster.reset.services

import com.festive.poster.models.GetFavTemplateResponse
import com.festive.poster.models.response.*
import com.festive.poster.reset.EndPoints
import com.framework.base.BaseResponse
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
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


  @POST(EndPoints.GET_FAV_TEMPLATES)
  fun getFavTemp(
    @Body body:JsonObject?,
    ): Observable<Response<GetFavTemplateResponse>>

  @POST(EndPoints.FAV_TEMPLATE)
  fun favPoster(
    @Body body:JsonObject?,
    ): Observable<Response<BaseResponse>>

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

  @POST(EndPoints.UPDATE_PURCHASE_STATUS)
  fun updatePurchaseStatus(
    @Body body:JsonObject?,
  ): Observable<Response<BaseResponse>>

  @GET(EndPoints.GET_CATEGORIES)
  fun getCategories():Observable<Response<GetCategoryResponse>>


  @POST(EndPoints.GET_TEMPLATES_V2)
  fun getTemplatesV2(@Body body:GetTemplatesV2Body):Observable<Response<GetTemplatesResponseV2>>
}