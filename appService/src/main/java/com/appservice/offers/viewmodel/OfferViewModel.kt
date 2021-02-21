package com.appservice.offers.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.offers.models.OfferListingRequest
import com.appservice.rest.repository.OfferNowFloatsRepository
import com.appservice.rest.services.OffersNowFloatsRemoteData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import retrofit2.http.Body

class OfferViewModel : BaseViewModel() {
    fun getOfferListing(@Body request: OfferListingRequest?): LiveData<BaseResponse> {
        return OfferNowFloatsRepository.getOfferListing(request = request).toLiveData()
    }
}