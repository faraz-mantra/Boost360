package dev.patrickgold.florisboard.customization.network.repository

import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import dev.patrickgold.florisboard.customization.network.client.NfxFloatApiClient
import dev.patrickgold.florisboard.customization.network.service.AppBaseLocalService
import dev.patrickgold.florisboard.customization.network.service.NfxFloatRemoteData
import retrofit2.Response
import retrofit2.Retrofit

object NfxFloatRepository : AppBaseRepository<NfxFloatRemoteData, AppBaseLocalService>() {

  suspend fun getChannelsStatus(nowfloatsId: String?): Response<ChannelAccessStatusResponse> {
    return remoteDataSource.getChannelsStatus(nowfloatsId)
  }

  override fun getRemoteDataSourceClass(): Class<NfxFloatRemoteData> = NfxFloatRemoteData::class.java

  override fun getLocalDataSourceInstance(): AppBaseLocalService = AppBaseLocalService()

  override fun getApiClient(): Retrofit = NfxFloatApiClient.shared.retrofit
}
