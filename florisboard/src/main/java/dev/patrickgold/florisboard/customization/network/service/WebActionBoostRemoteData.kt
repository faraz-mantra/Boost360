package dev.patrickgold.florisboard.customization.network.service

import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import dev.patrickgold.florisboard.customization.network.EndPoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WebActionBoostRemoteData {

  @GET(EndPoints.GET_WHATSAPP_BUSINESS)
  suspend fun getWhatsAppBusiness(
    @Header("Authorization") auth: String,
    @Query("query") request: String?,
    @Query("limit") limit: Int? = 1
  ): Response<ChannelWhatsappResponse>
}