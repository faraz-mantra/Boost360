package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class GetStaffListingRequest(

	@field:SerializedName("FloatingPointTag")
	val floatingPointTag: String? = null,

	@field:SerializedName("Limit")
	val limit: Int? = null,

	@field:SerializedName("ServiceId")
	val serviceId: String? = null,

	@field:SerializedName("Offset")
	val offset: Int? = null
)
