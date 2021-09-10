package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.addProductImage.ProductImageRequest
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.gstProduct.ProductGstDetailRequest
import com.appservice.model.serviceProduct.gstProduct.update.ProductUpdateRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.rest.repository.AssuredWithFloatRepository
import com.appservice.rest.repository.KitWebActionRepository
import com.appservice.rest.repository.WithFloatRepository
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductViewModel : BaseViewModel() {
  fun createProduct(request: CatalogProduct?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.createProduct(request).toLiveData()
  }

    fun getProductListing(fpTag: String?, clientId: String?, skipBy: Int?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getProductListing(fpTag, clientId, skipBy).toLiveData()

    }
    fun updateProduct(request: ProductUpdate?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.updateProduct(request).toLiveData()
    }

  fun deleteService(request: DeleteProductRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.deleteProduct(request).toLiveData()
  }

  fun getAllProducts(map: Map<String, String>): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getAllProducts(map).toLiveData()
  }

  fun addUpdateProductImage(
    clientId: String?,
    requestType: String?,
    requestId: String?,
    totalChunks: Int?,
    currentChunkNumber: Int?,
    productId: String?,
    requestBody: RequestBody?,
  ): LiveData<BaseResponse> {
    return WithFloatTwoRepository.addUpdateImageProduct(
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
}