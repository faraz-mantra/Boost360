package dev.patrickgold.florisboard.customization.network.service

import dev.patrickgold.florisboard.customization.model.request.CreateOrderRequest
import dev.patrickgold.florisboard.customization.model.response.*
import dev.patrickgold.florisboard.customization.network.EndPoints.BUSINESS_UPDATES_LISTING
import dev.patrickgold.florisboard.customization.network.EndPoints.CREATE_PRODUCT_OFFER
import dev.patrickgold.florisboard.customization.network.EndPoints.PRODUCT_LISTING
import dev.patrickgold.florisboard.customization.network.EndPoints.USER_ALL_DETAILS
import retrofit2.Response
import retrofit2.http.*

interface BusinessFeaturesRemoteData {
    @GET(BUSINESS_UPDATES_LISTING)
    suspend fun getAllUpdates(@QueryMap queries: Map<String, String>): Response<Updates>

    @GET(PRODUCT_LISTING)
    suspend fun getAllProducts(@QueryMap queries: Map<String, String>): Response<List<Product>>

    @GET(USER_ALL_DETAILS)
    suspend fun getAllDetails(@Path("fpTag") fpTag: String?, @QueryMap queries: Map<String, String>): Response<CustomerDetails>

    @POST(CREATE_PRODUCT_OFFER)
    suspend fun createProductOffers(@Body request: CreateOrderRequest): Response<CreatedOffer>
}