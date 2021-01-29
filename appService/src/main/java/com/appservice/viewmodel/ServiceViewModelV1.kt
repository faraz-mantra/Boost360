package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.ProductDimensionRequest
import com.appservice.model.serviceProduct.Product
import com.appservice.model.serviceProduct.addProductImage.ProductImageRequest
import com.appservice.model.serviceProduct.addProductImage.deleteRequest.ProductImageDeleteRequest
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.gstProduct.ProductGstDetailRequest
import com.appservice.model.serviceProduct.gstProduct.update.ProductUpdateRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
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

    fun addUpdateImageProductService(clientId: String?, requestType: String?, requestId: String?, totalChunks: Int?, currentChunkNumber: Int?,
                                     productId: String?, requestBody: RequestBody?): LiveData<BaseResponse> {
        return NowfloatsApiRepository.addUpdateImageProductService(clientId, requestType, requestId, totalChunks,
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
//  fun addProductDetails(request:ProductDimensionRequest): LiveData<BaseResponse> {
//    return KitWebActionRepository.productAddData(request).toLiveData()
//  }
}