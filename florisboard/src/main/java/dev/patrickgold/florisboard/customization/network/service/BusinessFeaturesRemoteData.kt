package dev.patrickgold.florisboard.customization.network.service

import dev.patrickgold.florisboard.customization.model.request.CreateOrderRequest
import dev.patrickgold.florisboard.customization.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface BusinessFeaturesRemoteData {
    @GET("/Discover/v3/floatingPoint/bizFloats")
    suspend fun getAllUpdates(@QueryMap queries: Map<String, String>): Response<Updates>

    @GET("/Product/v1/GetListings")
    suspend fun getAllProducts(@QueryMap queries: Map<String, String>): Response<List<Product>>

    @GET("/discover/v2/floatingPoint/nf-web/{fpTag}")
    suspend fun getAllDetails(@Path("fpTag") fpTag: String?, @QueryMap queries: Map<String, String>): Response<CustomerDetails>

    @POST("/api/Offers/CreateOffer")
    suspend fun createProductOffers(@Body request: CreateOrderRequest): Response<CreatedOffer>
}