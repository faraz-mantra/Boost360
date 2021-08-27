package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.serviceProduct.addProductImage.ProductImageRequest
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.gstProduct.ProductGstDetailRequest
import com.appservice.model.serviceProduct.gstProduct.update.ProductUpdateRequest
import com.appservice.model.serviceTiming.AddServiceTimingRequest
import com.appservice.model.servicev1.DeleteSecondaryImageRequest
import com.appservice.model.servicev1.DeleteServiceRequest
import com.appservice.model.servicev1.ServiceModelV1
import com.appservice.model.servicev1.UploadImageRequest
import com.appservice.rest.repository.*
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ServiceViewModelV1 : BaseViewModel() {

  fun createService(request: ServiceModelV1?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.createService(request).toLiveData()
  }

  fun updateService(request: ServiceModelV1?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.updateService(request).toLiveData()
  }

  fun deleteService(request: DeleteServiceRequest?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.deleteService(request).toLiveData()
  }

  fun deleteSecondaryImage(request: DeleteSecondaryImageRequest?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.deleteSecondaryImage(request).toLiveData()
  }

  fun addPrimaryImage(request: UploadImageRequest?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.addPrimaryImage(request).toLiveData()
  }

  fun addSecondaryImage(request: UploadImageRequest?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.addSecondaryImage(request).toLiveData()
  }

  fun getServiceDetails(serviceId: String?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.getServiceDetail(serviceId).toLiveData()
  }

  fun addUpdateImageProductService(
    clientId: String?,
    requestType: String?,
    requestId: String?,
    totalChunks: Int?,
    currentChunkNumber: Int?,
    productId: String?,
    requestBody: RequestBody?,
  ): LiveData<BaseResponse> {
    return NowfloatsApiRepository.addUpdateImageProductService(
      clientId, requestType, requestId, totalChunks,
      currentChunkNumber, productId, requestBody
    ).toLiveData()
  }

  fun addProductGstDetail(request: ProductGstDetailRequest?): LiveData<BaseResponse> {
    return KitWebActionRepository.addProductGstDetail(request).toLiveData()
  }

  fun updateProductGstDetail(request: ProductUpdateRequest?): LiveData<BaseResponse> {
    return KitWebActionRepository.updateProductGstDetail(request).toLiveData()
  }

  fun getProductGstDetail(query: String?): LiveData<BaseResponse> {
    return KitWebActionRepository.getProductGstDetail(query).toLiveData()
  }

  fun uploadImageProfile(
    assetFileName: String?,
    file: MultipartBody.Part?
  ): LiveData<BaseResponse> {
    return KitWebActionRepository.uploadImageProfile(assetFileName, file).toLiveData()
  }

  fun addProductImage(request: ProductImageRequest?): LiveData<BaseResponse> {
    return KitWebActionRepository.addProductImage(request).toLiveData()
  }

  fun deleteProductImage(request: ProductImageDeleteRequest?): LiveData<BaseResponse> {
    return KitWebActionRepository.deleteProductImage(request).toLiveData()
  }

  fun getProductImage(query: String?): LiveData<BaseResponse> {
    return KitWebActionRepository.getProductImage(query).toLiveData()
  }

  fun getPickUpAddress(fpId: String?): LiveData<BaseResponse> {
    return AssuredWithFloatRepository.getPickUpAddress(fpId).toLiveData()
  }

  fun userAccountDetails(fpId: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatRepository.userAccountDetail(fpId, clientId).toLiveData()
  }

  fun addServiceTiming(request: AddServiceTimingRequest?): LiveData<BaseResponse> {
    return StaffNowFloatsRepository.addServiceTiming(request).toLiveData()
  }

  fun updateServiceTiming(request: AddServiceTimingRequest?): LiveData<BaseResponse> {
    return StaffNowFloatsRepository.updateServiceTiming(request).toLiveData()
  }

  fun getServiceTiming(request: String?): LiveData<BaseResponse> {
    return StaffNowFloatsRepository.getServiceTiming(request).toLiveData()
  }

//  fun addProductDetails(request:ProductDimensionRequest): LiveData<BaseResponse> {
//    return KitWebActionRepository.productAddData(request).toLiveData()
//  }

  fun getSearchListings(fpTag: String?, fpId: String?, searchString: String? = "", offset: Int? = 0, limit: Int? = 0): LiveData<BaseResponse> {
    return NowfloatsApiRepository.getServiceSearchListing(fpTag, fpId, searchString, offset, limit).toLiveData()
  }
}