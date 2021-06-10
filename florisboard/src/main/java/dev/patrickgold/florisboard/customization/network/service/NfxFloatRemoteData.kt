package dev.patrickgold.florisboard.customization.network.service

import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import dev.patrickgold.florisboard.customization.network.EndPoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NfxFloatRemoteData {

  @GET(EndPoints.NFX_CHANNELS_STATUS)
  suspend fun getChannelsStatus(@Query("nowfloats_id") nowfloatsId: String?): Response<ChannelAccessStatusResponse>
}