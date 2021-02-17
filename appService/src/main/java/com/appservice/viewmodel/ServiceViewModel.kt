package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.addProductImage.ProductImageRequest
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.gstProduct.ProductGstDetailRequest
import com.appservice.model.serviceProduct.gstProduct.update.ProductUpdateRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.rest.repository.*
import com.appservice.ui.model.ServiceListingRequest
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ServiceViewModel : BaseViewModel() {

  fun createService(request: CatalogProduct?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.createService(request).toLiveData()
  }

  fun updateService(request: ProductUpdate?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.updateService(request).toLiveData()
  }

  fun deleteService(request: DeleteProductRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.deleteService(request).toLiveData()
  }

  fun addUpdateImageProductService(clientId: String?, requestType: String?, requestId: String?, totalChunks: Int?, currentChunkNumber: Int?,
                                   productId: String?, requestBody: RequestBody?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.addUpdateImageProductService(clientId, requestType, requestId, totalChunks,
        currentChunkNumber, productId, requestBody).toLiveData()
  }

  fun addProductGstDetail(auth: String?, request: ProductGstDetailRequest?): LiveData<BaseResponse> {
    return KitWebActionRepository.addProductGstDetail(auth, request).toLiveData()
  }

  fun updateProductGstDetail(auth: String?, request: ProductUpdateRequest?): LiveData<BaseResponse> {
    return KitWebActionRepository.updateProductGstDetail(auth, request).toLiveData()
  }

  fun getProductGstDetail(auth: String?, query: String?): LiveData<BaseResponse> {
    return KitWebActionRepository.getProductGstDetail(auth, query).toLiveData()
  }

  fun uploadImageProfile(auth: String?, assetFileName: String?, file: MultipartBody.Part?): LiveData<BaseResponse> {
    return KitWebActionRepository.uploadImageProfile(auth, assetFileName, file).toLiveData()
  }

  fun addProductImage(auth: String?, request: ProductImageRequest?): LiveData<BaseResponse> {
    return KitWebActionRepository.addProductImage(auth, request).toLiveData()
  }

  fun deleteProductImage(auth: String?, request: ProductImageDeleteRequest?): LiveData<BaseResponse> {
    return KitWebActionRepository.deleteProductImage(auth, request).toLiveData()
  }

  fun getProductImage(auth: String?, query: String?): LiveData<BaseResponse> {
    return KitWebActionRepository.getProductImage(auth, query).toLiveData()
  }

  fun getPickUpAddress(fpId: String?): LiveData<BaseResponse> {
    return AssuredWithFloatRepository.getPickUpAddress(fpId).toLiveData()
  }

  fun userAccountDetails(fpId: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatRepository.userAccountDetail(fpId, clientId).toLiveData()
  }

  fun getServiceListing(request: ServiceListingRequest): LiveData<BaseResponse> {
    return NowfloatsApiRepository.getServiceListing(request).toLiveData()
  }

  fun getSearchListings(fpTag: String?, fpId: String?, searchString: String?="", offset: Int?=0, limit: Int?=0):LiveData<BaseResponse>{
    return NowfloatsApiRepository.getServiceSearchListing(fpTag, fpId, searchString, offset, limit).toLiveData()
  }
}