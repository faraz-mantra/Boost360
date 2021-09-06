package dev.patrickgold.florisboard.customization.network.repository

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import dev.patrickgold.florisboard.customization.network.client.WebActionBoostApiClient
import dev.patrickgold.florisboard.customization.network.service.FlorisBoardLocalDataSource
import dev.patrickgold.florisboard.customization.network.service.WebActionBoostRemoteData
import retrofit2.Response
import retrofit2.Retrofit

object WebActionBoostRepository : AppBaseRepository<WebActionBoostRemoteData, FlorisBoardLocalDataSource>() {

  suspend fun getWhatsAppBusiness(auth: String, request: String?): Response<ChannelWhatsappResponse> {
    return remoteDataSource.getWhatsAppBusiness(auth, request)
  }

  suspend fun getBoostVisitingMessage(context: Context): BaseResponse {
    return localDataSource.getBoostVisitingMessage(context)
  }

  override fun getRemoteDataSourceClass(): Class<WebActionBoostRemoteData> = WebActionBoostRemoteData::class.java

  override fun getLocalDataSourceInstance(): FlorisBoardLocalDataSource = FlorisBoardLocalDataSource()

  override fun getApiClient(): Retrofit = WebActionBoostApiClient.shared.retrofit
}
