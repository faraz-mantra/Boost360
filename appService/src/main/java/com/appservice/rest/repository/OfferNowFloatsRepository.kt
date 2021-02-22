package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.offers.models.*
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.NowfloatsApiClient
import com.appservice.rest.services.OffersNowFloatsRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object OfferNowFloatsRepository : AppBaseRepository<OffersNowFloatsRemoteData, AppBaseLocalService>() {
    override fun getApiClient(): Retrofit {
        return NowfloatsApiClient.shared.retrofit
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    override fun getRemoteDataSourceClass(): Class<OffersNowFloatsRemoteData> {
        return OffersNowFloatsRemoteData::class.java
    }

    fun getOfferListing(request: OfferListingRequest?): Observable<BaseResponse> {
        return OfferNowFloatsRepository.makeRemoteRequest(OfferNowFloatsRepository.remoteDataSource.getOfferListing(request), TaskCode.GET_OFFER_LIST)
    }

    fun createOffer(request: OfferModel?): Observable<BaseResponse> {
        return OfferNowFloatsRepository.makeRemoteRequest(OfferNowFloatsRepository.remoteDataSource.createOffer(request), TaskCode.CREATE_OFFER)
    }
    fun updateOffer(request: OfferModel?): Observable<BaseResponse> {
        return OfferNowFloatsRepository.makeRemoteRequest(OfferNowFloatsRepository.remoteDataSource.updateOffer(request), TaskCode.UPDATE_OFFER)
    }
    fun addOfferImage(request: AddImageOffer?): Observable<BaseResponse> {
        return OfferNowFloatsRepository.makeRemoteRequest(OfferNowFloatsRepository.remoteDataSource.addOfferImage(request), TaskCode.ADD_OFFER_IMAGE)
    }
    fun getOfferDetails(request: OfferDetailsRequest?): Observable<BaseResponse> {
        return OfferNowFloatsRepository.makeRemoteRequest(OfferNowFloatsRepository.remoteDataSource.getOfferDetails(request), TaskCode.OFFER_DETAILS)
    }
    fun deleteOffer(request: DeleteOfferRequest?): Observable<BaseResponse> {
        return OfferNowFloatsRepository.makeRemoteRequest(OfferNowFloatsRepository.remoteDataSource.deleteOffer(request), TaskCode.DELETE_OFFER)
    }
}