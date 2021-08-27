package dev.patrickgold.florisboard.customization.network.repository

import com.onboarding.nowfloats.model.profile.MerchantProfileResponse
import dev.patrickgold.florisboard.customization.network.client.BoostFloatApiClient
import dev.patrickgold.florisboard.customization.network.service.AppBaseLocalService
import dev.patrickgold.florisboard.customization.network.service.BoostFloatRemoteData
import retrofit2.Response
import retrofit2.Retrofit

object BoostFloatRepository : AppBaseRepository<BoostFloatRemoteData, AppBaseLocalService>() {

  suspend fun getMerchantProfile(floatingId: String?): Response<MerchantProfileResponse> {
    return remoteDataSource.getMerchantProfile(floatingId)
  }

  override fun getRemoteDataSourceClass(): Class<BoostFloatRemoteData> = BoostFloatRemoteData::class.java

  override fun getLocalDataSourceInstance(): AppBaseLocalService = AppBaseLocalService()

  override fun getApiClient(): Retrofit = BoostFloatApiClient.shared.retrofit
}
