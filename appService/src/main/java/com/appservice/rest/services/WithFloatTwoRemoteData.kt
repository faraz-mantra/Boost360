package com.appservice.rest.services

import com.appservice.model.MerchantSummaryResponse
import com.appservice.model.panGst.PanGstUpdateBody
import com.appservice.model.VmnCallModel
import com.appservice.model.aptsetting.*
import com.appservice.model.businessmodel.BusinessProfileUpdateRequest
import com.appservice.model.panGst.PanGstDetailResponse
import com.appservice.model.product.ProductItemsResponseItem
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.CatalogProductCountResponse
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.model.updateBusiness.BusinessUpdateResponse
import com.appservice.model.updateBusiness.DeleteBizMessageRequest
import com.appservice.model.updateBusiness.PostUpdateTaskRequest
import com.appservice.model.updateBusiness.pastupdates.PastUpdatesNewListingResponse
import com.appservice.model.updateBusiness.pastupdates.TagListRequest
import com.appservice.rest.EndPoints
import com.framework.pref.clientId
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WithFloatTwoRemoteData {

  @POST(EndPoints.PAN_GST_UPDATE)
  fun panGstUpdate(@Body panGstUpdateBody: PanGstUpdateBody): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_PAN_GST_DETAILS)
  fun getPanGstDetail(
    @Path("fpId") fpId: String?,
    @Query("clientId") clientId: String?
  ): Observable<Response<PanGstDetailResponse>>


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

  @GET(EndPoints.GET_PRODUCT_LISTING)
  fun getProductListing(@Query("fpTag") fpTag: String?, @Query("clientId") clientId: String?, @Query("skipBy") skipBy: Int?): Observable<Response<List<CatalogProduct>>>

  @GET(EndPoints.GET_PRODUCT_LISTING_COUNT)
  fun getProductListingCount(@Query("fpTag") fpTag: String?, @Query("clientId") clientId: String?, @Query("skipBy") skipBy: Int?, @Query("identifierType") identifierType: String? = "SINGLE"): Observable<Response<CatalogProductCountResponse>>

  @GET(EndPoints.GET_NOTIFICATION)
  fun getNotificationCount(
    @Query("clientId") clientId: String?,
    @Query("fpId") fpId: String?,
    @Query("isRead") isRead: Boolean = false
  ): Observable<Response<Any>>

  @GET(EndPoints.GET_LATEST_UPDATES)
  fun getMessageUpdates(@QueryMap map: Map<String?, String?>?): Observable<Response<BusinessUpdateResponse>>

  @POST(EndPoints.CREATE_PRODUCT)
  fun createProduct(@Body request: CatalogProduct?): Observable<Response<String>>

  @POST(EndPoints.POST_PRODUCT_CATEGORY_VERB)
  fun updateProductCategoryVerb(@Body request: ProductCategoryVerbRequest?): Observable<Response<ResponseBody>>

  @PUT(EndPoints.UPDATE_PRODUCT)
  fun updateProduct(@Body request: ProductUpdate?): Observable<Response<ResponseBody>>

  @HTTP(method = "DELETE", path = EndPoints.DELETE_PRODUCT, hasBody = true)
  fun deleteProduct(@Body request: DeleteProductRequest?): Observable<Response<String>>

  @Headers("Accept: application/json", "Content-Type: application/octet-stream")
  @PUT(EndPoints.ADD_IMAGE)
  fun addUpdateImageProduct(
    @Query("clientId") clientId: String? = null,
    @Query("requestType") requestType: String? = null,
    @Query("requestId") requestId: String? = null,
    @Query("totalChunks") totalChunks: Int? = null,
    @Query("currentChunkNumber") currentChunkNumber: Int? = null,
    @Query("productId") productId: String? = null,
    @Query("identifierType") identifierType: String? = "SINGLE",
    @Query("sharingPlatforms") sharingPlatforms: String? = "",
    @Query("fileName") fileName: String? = null,
    @Body requestBody: RequestBody? = null,
  ): Observable<Response<String>>

  @GET(EndPoints.GET_FP_DETAILS)
  fun getFpDetails(
    @Path("fpid") fpid: String,
    @QueryMap map: Map<String, String>,
  ): Observable<Response<UserFpDetailsResponse>>

  @GET(EndPoints.BACKGROUND_IMAGE)
  fun getBackgroundImages(@Query("fpId") fpId: String?, @Query("clientId") clientId: String?): Observable<Response<Array<String>>>

  @PUT(EndPoints.PUT_BIZ_MESSAGE)
  fun putBizMessageUpdate(@Body request: PostUpdateTaskRequest?): Observable<Response<Any>>

  @PUT(EndPoints.PUT_BIZ_MESSAGE_V2)
  fun putBizMessageUpdateV2(@Body request: PostUpdateTaskRequest?): Observable<Response<Any>>

  @GET(EndPoints.GET_CATALOG_STATUS)
  fun getCatalogStatus(
    @Path("fpid") fpid: String,
    @Query("clientId") clientId: String?,
  ): Observable<Response<AppointmentStatusResponse>>

  @POST(EndPoints.UPDATE_GST_SLAB)
  fun updateGstSlab(
    @Body request: GstSlabRequest,
  ): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_BIZ_WEB_UPDATE_BY_ID)
  fun getBizWebMessage(
    @Path("id") id: String?,
    @Query("clientId") clientId: String?
  ): Observable<Response<ResponseBody>>

  @HTTP(method = "DELETE", path = EndPoints.DELETE_BIZ_MESSAGE_UPDATE, hasBody = true)
  fun deleteBizMessageUpdate(@Body request: DeleteBizMessageRequest?): Observable<Response<ResponseBody>>

  @Headers("Accept: application/json", "Content-Type: application/octet-stream")
  @PUT(EndPoints.PUT_BIZ_IMAGE)
  fun putBizImageUpdate(
    @Query("clientId") clientId: String?,
    @Query("requestType") requestType: String?,
    @Query("requestId") requestId: String?,
    @Query("totalChunks") totalChunks: Int?,
    @Query("currentChunkNumber") currentChunkNumber: Int?,
    @Query("socialParmeters") socialParmeters: String?,
    @Query("bizMessageId") bizMessageId: String?,
    @Query("sendToSubscribers") sendToSubscribers: Boolean?,
    @Body requestBody: RequestBody?,
  ): Observable<Response<String>>

  @POST(EndPoints.PUT_BIZ_IMAGE_V2)
  fun putBizImageUpdateV2(
    @Body body: JsonObject
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

  //TODO Ecommerce settings
  @POST(EndPoints.ADD_WARE_HOUSE)
  fun addWareHouse(@Body request: RequestAddWareHouseAddress): Observable<Response<ResponseBody>>

  @GET(EndPoints.GET_WARE_HOUSE + "/{fpId}/")
  fun getWareHouseAddress(
    @Path("fpId") fpId: String?,
    @Query("clientId") clientId: String?,
    @Query("offset") offset: Int? = 0,
    @Query("limit") limit: Int? = 200
  ): Observable<Response<GetWareHouseResponse>>

  @GET(EndPoints.GET_PRODUCT_LIST)
  fun getAllProducts(@QueryMap map: Map<String, String>): Observable<Response<Array<ProductItemsResponseItem>>>


  @GET(EndPoints.GET_MERCHANT_SUMMARY)
  fun getMerchantSummary(
    @Query("clientId") clientId: String?,
    @Query("fpTag") fpTag: String?
  ): Observable<Response<MerchantSummaryResponse>>

  @PUT(EndPoints.CREATE_BG_IMAGE)
  fun createBGImage(
    @Query("fpId") fpTag: String?,
    @Query("clientId") cId: String? = clientId,
    @Body body: RequestBody,
  ): Observable<Response<ResponseBody>>

  @POST(EndPoints.DELETE_BG_IMAGE)
  fun deleteBackgroundImages(@Body map: HashMap<String, String?>): Observable<Response<ResponseBody>>

  @PUT(EndPoints.POST_PAYMENT_ACCEPT_PROFILE)
  fun addUpdatePaymentProfile(@Body request: AddPaymentAcceptProfileRequest?): Observable<Response<ResponseBody>>

  @GET("/Wildfire/v1/calls/tracker")
  fun trackerCalls(@QueryMap data: Map<String, String?>?): Observable<Response<ArrayList<VmnCallModel?>?>>

  @POST(EndPoints.GET_PAST_UPDATES_LIST_V6)
  fun getPastUpdatesListV6(
    @Query("clientId") clientId: String?,
    @Query("fpId") fpId: String?,
    @Query("postType") postType: Int?,
    @Query("skipBy") skipBy: Int?,
    @Body request: TagListRequest
  ): Observable<Response<PastUpdatesNewListingResponse>>

  @POST
  fun updateBusinessProfile(@Url url:String,@Body profileUpdateRequest: BusinessProfileUpdateRequest): Observable<Response<ResponseBody>>
}