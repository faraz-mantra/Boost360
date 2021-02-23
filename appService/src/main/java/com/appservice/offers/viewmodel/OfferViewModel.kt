package com.appservice.offers.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.offers.models.*
import com.appservice.rest.repository.OfferNowFloatsRepository
import com.appservice.rest.repository.StaffNowFloatsRepository
import com.appservice.staffs.model.ServiceListRequest
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



}