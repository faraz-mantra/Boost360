package dev.patrickgold.florisboard.customization.network.repository

import com.appservice.model.serviceProduct.service.ServiceSearchListingResponse
import dev.patrickgold.florisboard.customization.model.response.ProductResponse
import dev.patrickgold.florisboard.customization.model.response.staff.GetStaffListingRequest
import dev.patrickgold.florisboard.customization.model.response.staff.StaffListingResponse
import dev.patrickgold.florisboard.customization.network.client.NowFloatApiClient
import dev.patrickgold.florisboard.customization.network.service.AppBaseLocalService
import dev.patrickgold.florisboard.customization.network.service.NowFloatRemoteData
import retrofit2.Response
import retrofit2.Retrofit
import java.util.HashMap

object NowFloatRepository : AppBaseRepository<NowFloatRemoteData, AppBaseLocalService>() {

  suspend fun fetchStaffList(request: GetStaffListingRequest?): Response<StaffListingResponse> {
    return remoteDataSource.fetchStaffList(request)
  }

  suspend fun getAllServices(fpTag: String?, fpId: String?, searchString: String? = "", offset: Int? = 0, limit: Int? = 0): Response<ServiceSearchListingResponse> {
    return remoteDataSource.getServiceSearchListing(fpTag, fpId, searchString, offset, limit)
  }
  override fun getRemoteDataSourceClass(): Class<NowFloatRemoteData> = NowFloatRemoteData::class.java

  override fun getLocalDataSourceInstance(): AppBaseLocalService = AppBaseLocalService()

  override fun getApiClient(): Retrofit = NowFloatApiClient.shared.retrofit
}
