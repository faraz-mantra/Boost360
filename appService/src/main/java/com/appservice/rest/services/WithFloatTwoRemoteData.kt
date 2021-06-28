package com.appservice.rest.services

import com.appservice.appointment.model.*
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
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

//  fun getDeliveryDetails(): Observable<Response<ResponseBody>>

  //TODO APPOINTMENT
  @POST(EndPoints.ADD_MERCHANT_UPI)
  fun upiIdUpdate(@Body request: UpdateUPIRequest): Observable<Response<ResponseBody>>

  @POST(EndPoints.ACCEPT_COD)
  fun updateCODDetails(@Body request: RequestCODPreference): Observable<Response<ResponseBody>>

  @PUT(EndPoints.ADD_BANK_ACCOUNT + "/{fpId}/")
  fun addBankAccounts(@Path("fpId") fpId: String?, @Query("clientId") clientId: String?, @Body request: AddBankAccountRequest): Observable<Response<ResponseBody>>

  @POST(EndPoints.DELIVERY_SETUP)
  fun deliverySetupPost(@Body request: DeliverySetup): Observable<Response<ResponseBody>>

  @PUT(EndPoints.UPLOAD_MERCHANT_SIGNATURE)
  fun uploadMerchantSignature(@Body request: UploadMerchantSignature): Observable<Response<ResponseBody>>


  @POST(EndPoints.INVOICE_SETUP)
  fun invoiceSetupPost(@Body request: InvoiceSetupRequest?): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_DELIVERY_CONFIG + "/{fpId}/")
  fun deliverySetupGet(@Path("fpId") fpId: String?, @Query("clientId") clientId: String?): Observable<Response<DeliveryDetailsResponse>>

  @GET(EndPoints.GET_PAYMENT_PROFILE_DETAILS + "/{fpId}/")
  fun paymentProfileDetailsGet(@Path("fpId") fpId: String?, @Query("clientId") clientId: String?): Observable<Response<PaymentProfileResponse>>

}