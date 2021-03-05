package com.appservice.rest.services

import com.appservice.offers.models.*
import com.appservice.rest.EndPoints
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OffersNowFloatsRemoteData {
    @POST(EndPoints.POST_GET_OFFER_LISTING)
    public fun getOfferListing(@Body request: OfferListingRequest?): Observable<Response<OfferListingResponse>>
    @POST(EndPoints.POST_CREATE_OFFER)
    public fun createOffer(@Body request: OfferModel?): Observable<Response<OfferBaseResponse>>
    @POST(EndPoints.POST_UPDATE_OFFER)
    public fun updateOffer(@Body request: OfferModel?): Observable<Response<BaseResponse>>
    @POST(EndPoints.POST_OFFER_DELETE)
    public fun deleteOffer(@Body request: DeleteOfferRequest?): Observable<Response<BaseResponse>>
    @POST(EndPoints.POST_OFFER_ADD_IMAGE)
    public fun addOfferImage(@Body request: AddImageOffer?): Observable<Response<BaseResponse>>
    @POST(EndPoints.POST_OFFER_DETAILS)
    public fun getOfferDetails(@Body request: OfferDetailsRequest?): Observable<Response<OfferDetailsResponse>>

}