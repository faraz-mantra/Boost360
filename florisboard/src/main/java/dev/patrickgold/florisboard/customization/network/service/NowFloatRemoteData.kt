package dev.patrickgold.florisboard.customization.network.service

import com.appservice.model.serviceProduct.service.ServiceSearchListingResponse
import dev.patrickgold.florisboard.customization.model.response.staff.GetStaffListingRequest
import dev.patrickgold.florisboard.customization.model.response.staff.StaffListingResponse
import dev.patrickgold.florisboard.customization.network.EndPoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NowFloatRemoteData {
  @POST(EndPoints.GET_STAFF_LISTING)
  suspend fun fetchStaffList(@Body request: GetStaffListingRequest?): Response<StaffListingResponse>

  @GET(EndPoints.GET_SEARCH_LISTING)
  suspend  fun getServiceSearchListing(
    @Query("fpTag") fpTag: String?,
    @Query("fpId") fpId: String?,
    @Query("searchString") searchString: String?,
    @Query("offset") offset: Int?,
    @Query("limit") limit: Int?,
  ): Response<ServiceSearchListingResponse>
}