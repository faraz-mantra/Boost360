package dev.patrickgold.florisboard.customization.network.repository

import dev.patrickgold.florisboard.customization.model.response.staff.GetStaffListingRequest
import dev.patrickgold.florisboard.customization.model.response.staff.StaffListingResponse
import dev.patrickgold.florisboard.customization.network.client.NowFloatApiClient
import dev.patrickgold.florisboard.customization.network.service.AppBaseLocalService
import dev.patrickgold.florisboard.customization.network.service.NowFloatRemoteData
import retrofit2.Response
import retrofit2.Retrofit

object NowFloatRepository : AppBaseRepository<NowFloatRemoteData, AppBaseLocalService>() {

  suspend fun fetchStaffList(request: GetStaffListingRequest?): Response<StaffListingResponse> {
    return remoteDataSource.fetchStaffList(request)
  }

  override fun getRemoteDataSourceClass(): Class<NowFloatRemoteData> = NowFloatRemoteData::class.java

  override fun getLocalDataSourceInstance(): AppBaseLocalService = AppBaseLocalService()

  override fun getApiClient(): Retrofit = NowFloatApiClient.shared.retrofit
}
