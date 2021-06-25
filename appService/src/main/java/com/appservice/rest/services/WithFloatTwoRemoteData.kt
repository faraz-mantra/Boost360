package com.appservice.rest.services

import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.model.updateBusiness.BusinessUpdateResponse
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WithFloatTwoRemoteData {

  @POST(EndPoints.CREATE_SERVICE)
  fun createService(@Body request: CatalogProduct?): Observable<Response<String>>

  @PUT(EndPoints.UPDATE_SERVICE)
  fun updateService(@Body request: ProductUpdate?): Observable<Response<ResponseBody>>

  @HTTP(method = "DELETE", path = EndPoints.DELETE_SERVICE, hasBody = true)
  fun deleteService(@Body request: DeleteProductRequest?): Observable<Response<String>>

  @GET(EndPoints.GET_TAGS)
  fun getTags(@Query("clientId") clientId: String?, @Query("fpId") fpId: String?): Observable<Response<List<String>>>

  @Headers("Accept: application/json", "Content-Type: application/octet-stream")
  @PUT(EndPoints.ADD_IMAGE)
  fun addUpdateImageProductService(
    @Query("clientId") clientId: String?,
    @Query("requestType") requestType: String?,
    @Query("requestId") requestId: String?,
    @Query("totalChunks") totalChunks: Int?,
    @Query("currentChunkNumber") currentChunkNumber: Int?,
    @Query("productId") productId: String?,
    @Body requestBody: RequestBody?,
  ): Observable<Response<String>>

  @GET(EndPoints.GET_NOTIFICATION)
  fun getNotificationCount(@Query("clientId") clientId: String?, @Query("fpId") fpId: String?, @Query("isRead") isRead: Boolean = false): Observable<Response<Any>>

  @GET(EndPoints.GET_LATEST_UPDATES)
  fun getMessageUpdates(@QueryMap map: Map<String?, String?>?): Observable<Response<BusinessUpdateResponse>>

  @POST(EndPoints.CREATE_PRODUCT)
  fun createProduct(@Body request: CatalogProduct?): Observable<Response<String>>

  @PUT(EndPoints.UPDATE_PRODUCT)
  fun updateProduct(@Body request: ProductUpdate?): Observable<Response<ResponseBody>>

  @HTTP(method = "DELETE", path = EndPoints.DELETE_PRODUCT, hasBody = true)
  fun deleteProduct(@Body request: DeleteProductRequest?): Observable<Response<String>>

  @Headers("Accept: application/json", "Content-Type: application/octet-stream")
  @PUT(EndPoints.ADD_IMAGE)
  fun addUpdateImageProduct(
    @Query("clientId") clientId: String?,
    @Query("requestType") requestType: String?,
    @Query("requestId") requestId: String?,
    @Query("totalChunks") totalChunks: Int?,
    @Query("currentChunkNumber") currentChunkNumber: Int?,
    @Query("productId") productId: String?,
    @Body requestBody: RequestBody?,
  ): Observable<Response<String>>
}