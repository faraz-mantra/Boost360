package com.appservice.model.staffModel

import com.google.gson.annotations.SerializedName

data class GetStaffListingRequest(
	@field:SerializedName("FilterBy")
	val filterBy: FilterBy? = null,
	@field:SerializedName("FloatingPointTag")
	val floatingPointTag: String? = null,
	@field:SerializedName("ServiceId")
	val serviceId: String? = null
)
