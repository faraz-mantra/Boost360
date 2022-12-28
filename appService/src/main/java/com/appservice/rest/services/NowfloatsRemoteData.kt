package com.appservice.rest.services

import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.model.servicev1.*
import com.appservice.rest.EndPoints
import com.appservice.model.serviceProduct.service.ServiceListingRequest
import com.appservice.model.serviceProduct.service.ServiceListingResponse
import com.appservice.model.serviceProduct.service.ServiceSearchListingResponse
import com.appservice.model.testimonial.AddTestimonialImageRequest
import com.appservice.model.testimonial.AddUpdateTestimonialRequest
import com.appservice.model.testimonial.ListTestimonialRequest
import com.appservice.model.testimonial.TestimonialAddResponse
import com.appservice.model.testimonial.response.TestimonialListResponse
import com.appservice.model.updateBusiness.pastupdates.GetCategoryResponse
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*

interface NowfloatsRemoteData {

  @POST(EndPoints.CREATE_SERVICE_V1)
  fun createService(@Body request: ServiceModelV1?): Observable<Response<ServiceV1BaseResponse>>

  @POST(EndPoints.ADD_IMAGE_V1)
  fun addImage(@Body request: UploadImageRequest?): Observable<Response<ServiceV1BaseResponse>>

  @POST(EndPoints.UPDATE_SERVICE_V1)
  fun updateService(@Body request: ServiceModelV1?): Observable<Response<ServiceV1BaseResponse>>

  @GET(EndPoints.GET_SERVICE_DETAILS)
  fun getServiceDetails(
    @Query("serviceId") serviceId: String?,
  ): Observable<Response<ServiceDetailResponse>>

  @GET(EndPoints.GET_SEARCH_LISTING)
  fun getServiceSearchListing(
    @Query("fpTag") fpTag: String?,
    @Query("fpId") fpId: String?,
    @Query("searchString") searchString: String?,
    @Query("offset") offset: Int?,
    @Query("limit") limit: Int?,
  ): Observable<Response<ServiceSearchListingResponse>>

  @POST(EndPoints.GET_SERVICE_LISTING)
  fun getServiceListing(
    @Body request: ServiceListingRequest,
  ): Observable<Response<ServiceListingResponse>>

  @HTTP(method = "POST", path = EndPoints.DELETE_SERVICE_V1, hasBody = true)
  fun deleteService(@Body request: DeleteServiceRequest?): Observable<Response<ServiceV1BaseResponse>>

  @GET(EndPoints.GET_TAGS)
  fun getTags(
    @Query("clientId") clientId: String?,
    @Query("fpId") fpId: String?
  ): Observable<Response<List<String>>>

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
  fun getNotificationCount(
    @Query("clientId") clientId: String?,
    @Query("fpId") fpId: String?,
    @Query("isRead") isRead: Boolean = false
  ): Observable<Response<Any>>

  @POST(EndPoints.CREATE_PRODUCT)
  fun createProduct(@Body request: CatalogProduct?): Observable<Response<String>>

//    @GET(EndPoints.GET_PRODUCT_LISTING)
//    fun getAllProducts(
//            @Query("clientId") clientId: String?,
//            @Query("skipBy") skipBy: Int?,
//            @Query("fpTag") fpTag: String?,
//    )

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

  @POST(EndPoints.DELETE_SECONDARY_IMAGE)
  fun deleteSecondaryImage(@Body request: DeleteSecondaryImageRequest?): Observable<Response<ServiceV1BaseResponse>>

  //TODO Testimonial
  @POST(EndPoints.GET_TESTIMONIAL_LIST)
  fun getTestimonialList(@Body request: ListTestimonialRequest?): Observable<Response<TestimonialListResponse>>

  @GET(EndPoints.GET_TESTIMONIAL_DETAIL)
  fun getTestimonialDetail(@Query("testimonialId") testimonialId: String?): Observable<Response<Any>>

  @POST(EndPoints.ADD_TESTIMONIAL)
  fun addTestimonial(@Body request: AddUpdateTestimonialRequest?): Observable<Response<TestimonialAddResponse>>

  @POST(EndPoints.UPDATE_TESTIMONIAL)
  fun updateTestimonial(@Body request: AddUpdateTestimonialRequest?): Observable<Response<TestimonialAddResponse>>

  @POST(EndPoints.ADD_IMAGE_TESTIMONIAL)
  fun updateTestimonialImage(@Body request: AddTestimonialImageRequest?): Observable<Response<Any>>

  @POST(EndPoints.DELETE_TESTIMONIAL)
  fun deleteTestimonial(@Body request: MutableMap<String, String>?): Observable<Response<Any>>

  @POST(EndPoints.DELETE_IMAGE_TESTIMONIAL)
  fun deleteTestimonialImage(@Body request: MutableMap<String, String>?): Observable<Response<Any>>

  @GET(EndPoints.GET_UPDATES_CATEGORIES)
  fun getUpdatesCategories():Observable<Response<GetCategoryResponse>>
}