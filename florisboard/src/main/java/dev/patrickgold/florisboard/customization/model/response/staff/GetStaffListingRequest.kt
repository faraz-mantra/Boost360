package dev.patrickgold.florisboard.customization.model.response.staff

import com.google.gson.annotations.SerializedName

data class GetStaffListingRequest(
  @field:SerializedName("FilterBy")
  val filterBy: FilterBy? = null,
  @field:SerializedName("FloatingPointTag")
  val floatingPointTag: String? = null,
  @field:SerializedName("ServiceId")
  val serviceId: String? = null
)

data class FilterBy(
  @field:SerializedName("ServiceType")
  val serviceType: String? = null,
  @field:SerializedName("Limit")
  val limit: Int? = null,
  @field:SerializedName("Offset")
  var offset: Int? = null,
)