package com.appservice.offers.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.staffModel.ServiceListRequest
import com.appservice.offers.models.*
import com.appservice.rest.repository.NowfloatsApiRepository
import com.appservice.rest.repository.OfferNowFloatsRepository
import com.appservice.rest.repository.StaffNowFloatsRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import retrofit2.http.Body

class OfferViewModel : BaseViewModel() {
    fun getOfferListing(@Body request: OfferListingRequest?): LiveData<BaseResponse> {
        return OfferNowFloatsRepository.getOfferListing(request = request).toLiveData()
    }

    fun createOffer(@Body request: OfferModel?): LiveData<BaseResponse> {
        return OfferNowFloatsRepository.createOffer(request = request).toLiveData()
    }

    fun updateOffer(@Body request: OfferModel?): LiveData<BaseResponse> {
        return OfferNowFloatsRepository.updateOffer(request = request).toLiveData()
    }

    fun getOfferDetails(offerId: OfferDetailsRequest?): LiveData<BaseResponse> {
        return OfferNowFloatsRepository.getOfferDetails(offerId).toLiveData()
    }

    fun addOfferImages(request: AddImageOffer?): LiveData<BaseResponse> {
        return OfferNowFloatsRepository.addOfferImage(request = request).toLiveData()
    }

    fun deleteOffer(request: DeleteOfferRequest?): LiveData<BaseResponse> {
        return OfferNowFloatsRepository.deleteOffer(request = request).toLiveData()
    }
    fun getServiceListing(@Body request: ServiceListRequest?): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.getServicesListing(request = request).toLiveData()
    }
    fun getServiceDetails(serviceId: String?): LiveData<BaseResponse> {
        return NowfloatsApiRepository.getServiceDetail(serviceId).toLiveData()
    }

    fun getSearchListings(fpTag: String?, fpId: String?, searchString: String?="", offset: Int?=0, limit: Int?=0):LiveData<BaseResponse>{
        return NowfloatsApiRepository.getServiceSearchListing(fpTag, fpId, searchString, offset, limit).toLiveData()
    }
}