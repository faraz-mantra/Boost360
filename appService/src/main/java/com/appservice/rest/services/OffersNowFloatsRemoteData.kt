package com.appservice.rest.services

import com.appservice.offers.models.OfferListingRequest
import com.appservice.offers.models.OfferListingResponse
import com.appservice.rest.EndPoints
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface OffersNowFloatsRemoteData {
    @POST(EndPoints.POST_GET_OFFER_LISTING)
    public fun getOfferListing(@Body request: OfferListingRequest?): Observable<Response<OfferListingResponse>>
}