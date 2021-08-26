package dev.patrickgold.florisboard.customization.network.service

import dev.patrickgold.florisboard.customization.model.response.staff.GetStaffListingRequest
import dev.patrickgold.florisboard.customization.model.response.staff.StaffListingResponse
import dev.patrickgold.florisboard.customization.network.EndPoints
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NowFloatRemoteData {
  @POST(EndPoints.GET_STAFF_LISTING)
  suspend fun fetchStaffList(@Body request: GetStaffListingRequest?): Response<StaffListingResponse>
}