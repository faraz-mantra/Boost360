package com.appservice.rest.services

import com.appservice.model.MerchantSummaryResponse
import com.appservice.model.VmnCallModel
import com.appservice.model.aptsetting.*
import com.appservice.model.product.ProductItemsResponseItem
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.CatalogProductCountResponse
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.model.updateBusiness.BusinessUpdateResponse
import com.appservice.model.updateBusiness.DeleteBizMessageRequest
import com.appservice.model.updateBusiness.PostUpdateTaskRequest
import com.appservice.rest.EndPoints
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RiaMemoryWithFloatsRemoteData {

/*  @GET("/Wildfire/v1/calls/GetLastCallLogInfoWithRange")
  fun getLastCallInfo(
    @QueryMap data: Map<*, *>?,
  ):Observable<Response<ArrayList<VmnCallModel?>?>>*/

/*  @GET("/WildFire/v1/calls/summary")
  fun getVmnSummary(
    @Query("clientId") clientId: String?,
    @Query("fpid") fpId: String?,
    @Query("identifierType") type: String?,
  ):Observable<Response<JsonObject?>>*/

  @GET("/memory/api/fpactivity/countfpactivity")
  fun getCallCountByType(
    @Query("fptag") fptag: String?,
    @Query("eventType") eventType: String?,
    @Query("eventChannel") eventChannel: String?,
  ):Observable<Response<JsonObject?>>

/*  @POST("/api/Service/EmailRIASupportTeamV2")
  fun requestVmn(
    @Body bodyMap: Map<String?, String?>?,
    @Query("authClientId") clientId: String?,
    @Query("fpTag") fpTag: String?,
  ):Observable<Response<Boolean>>*/

}