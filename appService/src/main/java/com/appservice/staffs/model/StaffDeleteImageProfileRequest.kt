package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffDeleteImageProfileRequest(

	@field:SerializedName("StaffId")
	val staffId: String? = null,
	@field:SerializedName("FloatingPointTag")
	val floatingPointTag: String? = null
)
