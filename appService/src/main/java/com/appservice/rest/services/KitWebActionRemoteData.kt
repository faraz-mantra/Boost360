package com.appservice.rest.services

import com.appservice.model.onboardingUpdate.OnBoardingUpdateModel
import com.appservice.model.serviceProduct.addProductImage.ProductImageRequest
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.addProductImage.response.ProductImageResponse
import com.appservice.model.serviceProduct.gstProduct.ProductGstDetailRequest
import com.appservice.model.serviceProduct.gstProduct.response.ProductGstResponse
import com.appservice.model.serviceProduct.gstProduct.update.ProductUpdateRequest
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface KitWebActionRemoteData {

  @POST(EndPoints.ADD_PRODUCT_DETAIL)
  fun addProductGstDetail(@Body request: ProductGstDetailRequest?): Observable<Response<ResponseBody>>

  @POST(EndPoints.UPDATE_PRODUCT_DETAIL)
  fun updateProductGstDetail(@Body request: ProductUpdateRequest?): Observable<Response<ResponseBody>>

  //kotlin.String.format("{'product_id':'%s'}", productId)
  @GET(EndPoints.GET_PRODUCT_DETAIL)
  fun getProductGstDetail(@Query("query") query: String?): Observable<Response<ProductGstResponse>>


  @Multipart
  @POST(EndPoints.UPLOAD_FILE_PRODUCT)
  fun uploadImageProfile(
      @Query("assetFileName") assetFileName: String?,
      @Part file: MultipartBody.Part?,
  ): Observable<Response<ResponseBody>>

  @POST(EndPoints.ADD_PRODUCT_IMAGE)
  fun addProductImage(@Body request: ProductImageRequest?): Observable<Response<ResponseBody>>

  @HTTP(method = "DELETE", path = EndPoints.DELETE_PRODUCT_IMAGE, hasBody = true)
  fun deleteProductImage(@Body request: ProductImageDeleteRequest?): Observable<Response<ResponseBody>>

  //String.format("{'_pid':'%s'}", productId)
  @GET(EndPoints.GET_PRODUCT_IMAGE)
  fun getProductImage(@Query("query") query: String?): Observable<Response<ProductImageResponse>>

  @POST(EndPoints.FP_ONBOARDING_UPDATE_DATA)
  fun fpOnboardingUpdate(@Body request: OnBoardingUpdateModel?): Observable<Response<ResponseBody>>

}