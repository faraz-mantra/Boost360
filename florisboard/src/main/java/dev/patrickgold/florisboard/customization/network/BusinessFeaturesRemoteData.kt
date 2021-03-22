package dev.patrickgold.florisboard.customization.network

import dev.patrickgold.florisboard.customization.model.request.CreateOrderRequest
import dev.patrickgold.florisboard.customization.model.response.CreatedOffer
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.customization.model.response.Updates
import retrofit2.http.*

interface BusinessFeaturesRemoteData {
    @GET("/Discover/v3/floatingPoint/bizFloats")
    suspend fun getAllUpdates(@QueryMap queries: Map<String, String>): Updates

    @GET("/Product/v1/GetListings")
    suspend fun getAllProducts(@QueryMap queries: Map<String, String>): List<Product>

    @GET("/discover/v2/floatingPoint/nf-web/{fpTag}")
    suspend fun getAllDetails(@Path("fpTag") fpTag: String, @QueryMap queries: Map<String, String>): CustomerDetails

    @POST("/api/Offers/CreateOffer")
    suspend fun createProductOffers(@Body request: CreateOrderRequest): CreatedOffer
}