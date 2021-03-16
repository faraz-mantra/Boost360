package com.appservice.staffs.model

import com.google.gson.annotations.SerializedName

data class StaffDeleteImageProfileRequest(

	@field:SerializedName("staffId",alternate = ["StaffId"])
	val staffId: String? = null,
	@field:SerializedName("floatingPointTag",alternate = ["FloatingPointTag"])
	val floatingPointTag: String? = null
)
