package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.ProductDimensionRequest
import com.appservice.model.onboardingUpdate.OnBoardingUpdateModel
import com.appservice.model.serviceProduct.addProductImage.ProductImageRequest
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.gstProduct.ProductGstDetailRequest
import com.appservice.model.serviceProduct.gstProduct.update.ProductUpdateRequest
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.KitWebActionApiClient
import com.appservice.rest.services.KitWebActionRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit

object KitWebActionRepository : AppBaseRepository<KitWebActionRemoteData, AppBaseLocalService>() {

  fun addProductGstDetail(request: ProductGstDetailRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.addProductGstDetail(
        "58ede4d4ee786c1604f6c535",
        request
      ), TaskCode.ADD_PRODUCT_GST_DETAIL
    )
  }

  fun updateProductGstDetail(request: ProductUpdateRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateProductGstDetail(
        "58ede4d4ee786c1604f6c535",
        request
      ), TaskCode.UPDATE_PRODUCT_GST_DETAIL
    )
  }

  fun getProductGstDetail(query: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getProductGstDetail(
        "58ede4d4ee786c1604f6c535",
        query
      ), TaskCode.GET_PRODUCT_GST_DETAIL
    )
  }

  fun uploadImageProfile(
    assetFileName: String?,
    file: MultipartBody.Part?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.uploadImageProfile(
        "58ede4d4ee786c1604f6c535",
        assetFileName,
        file
      ), TaskCode.UPLOAD_FILE_PRODUCT_IMAGE
    )
  }

  fun addProductImage(request: ProductImageRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.addProductImage("58ede4d4ee786c1604f6c535", request),
      TaskCode.ADD_PRODUCT_IMAGE
    )
  }

  fun deleteProductImage(request: ProductImageDeleteRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.deleteProductImage(
        "58ede4d4ee786c1604f6c535",
        request
      ), TaskCode.DELETE_PRODUCT_IMAGE
    )
  }

  fun getProductImage(query: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getProductImage("58ede4d4ee786c1604f6c535", query),
      TaskCode.GET_PRODUCT_IMAGE
    )
  }

//    fun fpOnboardingUpdate(request: OnBoardingUpdateModel?): Observable<BaseResponse> {
//        return makeRemoteRequest(remoteDataSource.fpOnboardingUpdate(request), TaskCode.FP_ONBOARDING_UPDATE)
//    }

  override fun getRemoteDataSourceClass(): Class<KitWebActionRemoteData> {
    return KitWebActionRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return KitWebActionApiClient.shared.retrofit
  }

//  fun productAddData(request: ProductDimensionRequest): Observable<BaseResponse> {
//    return makeRemoteRequest(remoteDataSource.addProductDimensionDetails(request),TaskCode.POST_UPDATE_PRODUCT)
//  }
}
